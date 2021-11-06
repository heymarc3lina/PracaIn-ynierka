package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.AllReservationDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreatedReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.SeatDto;
import com.agatapietrzycka.ticketreservation.controller.dto.SummaryCreatedReservationDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.Reservation;
import com.agatapietrzycka.ticketreservation.model.ReservationInformation;
import com.agatapietrzycka.ticketreservation.model.Seat;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.model.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationRepository;
import com.agatapietrzycka.ticketreservation.repository.SeatRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import com.agatapietrzycka.ticketreservation.util.exception.CustomReservationException;
import com.agatapietrzycka.ticketreservation.util.exception.CustomUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationInformationRepository reservationInformationRepository;

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

    @Transactional(rollbackFor = CustomReservationException.class)
    public SummaryCreatedReservationDto createReservation(CreateReservationDto createReservationDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomReservationException("User does not exist"));
        Flight flight = flightRepository.findById(createReservationDto.getFlightId()).orElseThrow(() -> new CustomFlightException("Flight does not exist"));
        LocalDateTime reservationDate = LocalDateTime.now();
        List<Seat> seats = seatRepository.findAllById(createReservationDto.getSeatList());
        SummaryCreatedReservationDto summaryCreatedReservationDto = new SummaryCreatedReservationDto();
        List<CreatedReservationDto> createdReservationDtos = new ArrayList<>();
        for (Seat seat : seats) {
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setFlight(flight);
            reservation.setSeat(seat);
            reservation.setReservationDate(reservationDate);
            ReservationInformation reservationInformation = new ReservationInformation();
            reservationInformation.setReservationStatus(ReservationStatus.WAITING);
            reservationInformation.setUpdatedAt(Instant.now());
            reservationInformation.setReservation(reservation);
            reservationInformationRepository.save(reservationInformation);
            reservation.setReservationInformation(reservationInformation);
            Reservation savedReservation = reservationRepository.save(reservation);
            CreatedReservationDto createdReservationDto = new CreatedReservationDto();
            createdReservationDto.setReservationId(reservation.getReservationId());
            createdReservationDto.setSuccess(savedReservation != null);
            createdReservationDtos.add(createdReservationDto);
        }
        summaryCreatedReservationDto.setReservationIdList(createdReservationDtos);
        summaryCreatedReservationDto.setSuccess(summaryCreatedReservationDto.getReservationIdList().stream().allMatch(CreatedReservationDto::isSuccess));

        if (!summaryCreatedReservationDto.isSuccess()) {
            throw new CustomUserException("We are sorry, but cannot make your reservation");
        }
        return summaryCreatedReservationDto;
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

    public List<AllReservationDataDto> getAllReservationForUsers() {
        List<Reservation> reservation = reservationRepository.findAll();
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
        reservationDto.setArrivalAirport(flight.getArrivalAirport().getCity());
        reservationDto.setDepartureAirport(flight.getDepartureAirport().getCity());
        reservationDto.setPlaneName(flight.getPlane().getName());
        reservationDto.setReservationDate(reservation.getReservationDate());
        reservationDto.setSeatNumber(reservation.getSeat().getSeatNumber());
        reservationDto.setReservationStatus(reservation.getReservationInformation().getReservationStatus());
        reservationDto.setArrivalDate(flight.getArrivalDate());
        reservationDto.setDepartureDate(flight.getDepartureDate());
        reservationDto.setReservationStatuses(getProperStatusList(reservation));
        return reservationDto;
    }

    private List<ReservationStatus> getProperStatusList(Reservation reservation) {
        List<ReservationStatus> reservationStatuses = null;
        if (reservation.getReservationInformation().getReservationStatus() == ReservationStatus.WAITING) {
            reservationStatuses = List.of(ReservationStatus.SUBMITTED, ReservationStatus.CANCELED);
        } else if (reservation.getReservationInformation().getReservationStatus() == ReservationStatus.SUBMITTED) {
            reservationStatuses = List.of(ReservationStatus.CANCELED);
        }
        return reservationStatuses;
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllUserReservation(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomReservationException("User does not exist"));

        List<Reservation> reservations = reservationRepository.findAllByUserId(user.getUserId());
        List<ReservationDto> reservationDtos = new ArrayList<>();
        reservations.forEach(reservation -> {
            if (reservation.getReservationInformation().getReservationStatus() != ReservationStatus.CANCELED) {
                reservationDtos.add(prepareReservationDto(reservation));
            }
        });
        return reservationDtos;
    }
}
