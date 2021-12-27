package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.AllReservationDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreatedReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.controller.dto.SeatDto;
import com.agatapietrzycka.ticketreservation.controller.dto.SummaryCreatedReservationDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.Reservation;
import com.agatapietrzycka.ticketreservation.model.ReservationInformation;
import com.agatapietrzycka.ticketreservation.model.Seat;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.model.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationRepository;
import com.agatapietrzycka.ticketreservation.repository.SeatRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import com.agatapietrzycka.ticketreservation.util.exception.CustomReservationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationInformationRepository reservationInformationRepository;
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public DataToReservationDto prepareDataToReservation(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        String arrivalAirport = flight.getArrivalAirport().getCity();
        String departureAirport = flight.getDepartureAirport().getCity();
        LocalDateTime arrivalDate = flight.getArrivalDate();
        LocalDateTime departureDate = flight.getDepartureDate();
        List<SeatDto> seatList = new ArrayList<>();
        Long planeId = flight.getPlane().getPlaneId();
        List<Seat> seats = seatRepository.findAllByPlaneId(planeId);
        List<Long> occupiedSeatsList = reservationRepository.findAllSeatForFligth(flightId);
        for (Seat seat : seats) {
            seatList.add(prepareSeatInformation(seat, occupiedSeatsList, flight.getPrice()));
        }
        return new DataToReservationDto(flightId, arrivalAirport, arrivalDate, departureAirport, departureDate, seatList);

    }

    private SeatDto prepareSeatInformation(Seat seat, List<Long> occupiedSeatsList, Integer price) {
        SeatDto seatDto = new SeatDto();
        seatDto.setId(seat.getSeatId());
        seatDto.setSeatNumber(seat.getSeatNumber());
        seatDto.setClassType(seat.getClassType().getClassType());
        seatDto.setIsAvailable(true);
        seatDto.setPrice(seat.getClassType().getCalculatePrice() * price);
        if (occupiedSeatsList.isEmpty()) {
            seatDto.setIsAvailable(true);
        } else {
            if (occupiedSeatsList.contains(seat.getSeatId())) {
                seatDto.setIsAvailable(false);
            }
        }
        return seatDto;
    }

    @Transactional(rollbackFor = CustomReservationException.class)
    public SummaryCreatedReservationDto createReservation(CreateReservationDto createReservationDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomReservationException("User does not exist"));
        Flight flight = flightRepository.findById(createReservationDto.getFlightId()).orElseThrow(() -> new CustomFlightException("Flight does not exist"));
        LocalDateTime reservationDate = LocalDateTime.now();
        List<Seat> seats = seatRepository.findAllById(createReservationDto.getSeatList());
        SummaryCreatedReservationDto summaryCreatedReservationDto = new SummaryCreatedReservationDto();
        List<CreatedReservationDto> createdReservationDtos = new ArrayList<>();
        for (Seat seat : seats) {
            Reservation savedReservation = createAndSaveReservation(user, flight, seat, reservationDate);
            CreatedReservationDto createdReservationDto = new CreatedReservationDto();
            createdReservationDto.setReservationId(savedReservation.getReservationId());
            createdReservationDto.setSuccess(savedReservation != null);
            createdReservationDtos.add(createdReservationDto);
        }
        summaryCreatedReservationDto.setReservationIdList(createdReservationDtos);
        summaryCreatedReservationDto.setSuccess(summaryCreatedReservationDto.getReservationIdList().stream().allMatch(CreatedReservationDto::isSuccess));

        if (!summaryCreatedReservationDto.isSuccess()) {
            throw new CustomReservationException("We are sorry, but cannot make your reservation");
        }

        validateIfPlaneStillAvailable(flight);
        emailService.sendReservationSumary(flight, seats, user, reservationDate, calculatePrice(seats, flight));
        return summaryCreatedReservationDto;
    }

    private double calculatePrice(List<Seat> seats, Flight flight) {
        double price = 0;
        List<Double> priceForSeat = new ArrayList<>();
        seats.forEach(e -> {
            priceForSeat.add(e.getClassType().getCalculatePrice() * flight.getPrice());
        });
        for (double p : priceForSeat) {
            price += p;
        }
        return price;
    }

    private Reservation createAndSaveReservation(User user, Flight flight, Seat seat, LocalDateTime reservationDate) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFlight(flight);
        reservation.setSeat(seat);
        reservation.setReservationDate(reservationDate);
        ReservationInformation reservationInformation = new ReservationInformation();
        reservationInformation.setReservationStatus(ReservationStatus.ACTIVE);
        reservationInformation.setUpdatedAt(Instant.now());
        reservationInformation.setReservation(reservation);
        reservationInformationRepository.save(reservationInformation);
        reservation.setReservationInformation(reservationInformation);
        return reservationRepository.save(reservation);
    }

    private void validateIfPlaneStillAvailable(Flight flight) {
        Integer occupiedSeats = reservationRepository.findAllSeatForFligth(flight.getId()).size();
        if (occupiedSeats == flight.getPlane().getPlace()) {
            flight.getFlightInformation().setStatus(FlightStatus.FULL);
            flight.getFlightInformation().setUpdatedAt(Instant.now());
        }
        if (occupiedSeats != flight.getPlane().getPlace() && flight.getFlightInformation().getStatus() == FlightStatus.FULL) {
            flight.getFlightInformation().setStatus(FlightStatus.AVAILABLE);
            flight.getFlightInformation().setUpdatedAt(Instant.now());
        }

    }

    public List<AllReservationDataDto> getAllReservations() {
        List<Reservation> reservation = reservationRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Reservation::getReservationDate).reversed())
                .collect(Collectors.toList());
        ;
        List<AllReservationDataDto> allReservationDataDtos = new ArrayList<>();
        reservation.forEach(e -> {
            AllReservationDataDto allReservationDataDto = new AllReservationDataDto();
            ReservationDto reservationDto = prepareReservationDto(e);

            User user = e.getUser();
            allReservationDataDto.setUserEmail(user.getEmail());
            allReservationDataDto.setUserName(user.getName());
            allReservationDataDto.setUserSurname(user.getSurname());
            allReservationDataDto.setReservationDto(reservationDto);
            allReservationDataDtos.add(allReservationDataDto);

        });
        return allReservationDataDtos;
    }

    private ReservationDto prepareReservationDto(Reservation reservation) {

        ReservationDto reservationDto = new ReservationDto();
        Flight flight = reservation.getFlight();
        reservationDto.setReservationId(reservation.getReservationId());
        reservationDto.setArrivalAirport(flight.getArrivalAirport().getCity());
        reservationDto.setDepartureAirport(flight.getDepartureAirport().getCity());
        reservationDto.setPlaneName(flight.getPlane().getName());
        reservationDto.setReservationDate(reservation.getReservationDate());
        reservationDto.setSeatNumber(reservation.getSeat().getSeatNumber());
        if (reservation.getReservationInformation().getReservationStatus().equals(ReservationStatus.ACTIVE) && compareDate(flight.getDepartureDate())) {
            reservation.getReservationInformation().setUpdatedAt(Instant.now());
            reservation.getReservationInformation().setReservationStatus(ReservationStatus.COMPLETED);
        }
        reservationDto.setReservationStatus(reservation.getReservationInformation().getReservationStatus());
        reservationDto.setArrivalDate(flight.getArrivalDate());
        reservationDto.setDepartureDate(flight.getDepartureDate());
        return reservationDto;
    }

    private Boolean compareDate(LocalDateTime departureDate) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("UTC"));

        int compareDeparture = departureDate.compareTo(currentTime);
        if (compareDeparture <= 0) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllUserReservation(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomReservationException("User does not exist"));

        List<Reservation> reservations = reservationRepository.findAllByUserId(user.getUserId())
                .stream()
                .sorted(Comparator.comparing(Reservation::getReservationDate)
                        .reversed()).collect(Collectors.toList());
        List<ReservationDto> reservationDtos = new ArrayList<>();
        reservations.forEach(reservation -> {
            reservationDtos.add(prepareReservationDto(reservation));
        });
        return reservationDtos;
    }

    @Transactional
    public ResponseDto cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new CustomReservationException("Reservation does not exist!"));

        ReservationInformation reservationInformation = reservation.getReservationInformation();
        List<String> errorMessage = validateReservation(reservationInformation);
        if (errorMessage.isEmpty()) {
            reservationInformation.setReservationStatus(ReservationStatus.CANCELED);
            reservationInformation.setUpdatedAt(Instant.now());
            validateIfPlaneStillAvailable(reservation.getFlight());
        }

        return new ResponseDto(reservation.getReservationId(), errorMessage);
    }

    private List<String> validateReservation(ReservationInformation reservationInformation) {
        List<String> errorMessages = new ArrayList<>();
        Validator validator = validatorFactory.getValidator();
        validator.validate(reservationInformation).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

}
