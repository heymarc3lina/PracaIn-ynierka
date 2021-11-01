package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ReservationDto;
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
import java.util.stream.Collectors;

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
        List<DataToReservationDto.ListOfSeat> seatList = new ArrayList<>();
        Long planeId = flight.getPlane().getPlaneId();
        List<Seat> seats = seatRepository.findAllByPlaneId(planeId);
        List<Long> occupiedSeatsList = reservationRepository.findAllSeatForFligth(flightId);
        for (Seat seat : seats) {
            seatList.add(prepareSeatInformation(seat, occupiedSeatsList, flight.getPrice()));

        }
        return new DataToReservationDto(flightId, arrivalAirport, arrivalDate, departureAirport, departureDate, seatList);

    }

    @Transactional(rollbackFor = CustomReservationException.class)
    public CreateReservationDto createReservation(ReservationDto reservationDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomReservationException("User does not exist"));
        Flight flight = flightRepository.findById(reservationDto.getFlightId()).orElseThrow(() -> new CustomFlightException("Flight does not exist"));
        Instant reservationDate = Instant.now();
        List<Seat> seats = seatRepository.findAllById(reservationDto.getSeatList().stream().map(ReservationDto.SeatList::getSeatId).collect(Collectors.toList()));
        CreateReservationDto createReservationDto = new CreateReservationDto();
        List<CreateReservationDto.ReservationList> reservationsList = new ArrayList<>();
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
            CreateReservationDto.ReservationList reservationList = new CreateReservationDto.ReservationList();
            reservationList.setReservationId(reservation.getReservationId());
            reservationList.setSuccess(savedReservation != null);
            reservationsList.add(reservationList);
        }
        createReservationDto.setReservationIdList(reservationsList);
        createReservationDto.setSuccess(!createReservationDto.getReservationIdList().stream().anyMatch(e -> e.isSuccess() == false));

        if (!createReservationDto.isSuccess()) {
            throw new CustomUserException("We are sorry, but cannot make your reservation");
        }
        return createReservationDto;
    }

    private DataToReservationDto.ListOfSeat prepareSeatInformation(Seat seat, List<Long> occupiedSeatsList, Integer price) {
        DataToReservationDto.ListOfSeat element = new DataToReservationDto.ListOfSeat();
        element.setId(seat.getSeatId());
        element.setSeatNumber(seat.getSeatNumber());
        element.setClassType(seat.getClassType().getClassType());
        element.setIsAvailable(true);
        element.setPrice(seat.getClassType().getCalculatePrice() * price);
        if (occupiedSeatsList.isEmpty()) {
            element.setIsAvailable(true);
        } else {
            if (occupiedSeatsList.contains(seat.getSeatId())) {
                element.setIsAvailable(false);
            }
        }
        return element;
    }
}
