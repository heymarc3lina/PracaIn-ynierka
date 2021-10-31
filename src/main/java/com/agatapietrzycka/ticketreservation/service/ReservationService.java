package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.Seat;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationRepository;
import com.agatapietrzycka.ticketreservation.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public DataToReservationDto prepareDataToReservation(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        String arrivalAirport = flight.getArrivalAirport().getCity();
        String departureAirport = flight.getDepartureAirport().getCity();
        Instant arrivalDate = flight.getArrivalDate();
        Instant departureDate = flight.getDepartureDate();
        List<DataToReservationDto.ListOfSeat> seatList = new ArrayList<>();
        Long planeId = flight.getPlane().getPlaneId();
        List<Seat> seats = seatRepository.findAllByPlaneId(planeId);
        List<Long> occupiedSeatsList = reservationRepository.findAllSeatForFligth(flightId);
        for (Seat seat : seats) {
            seatList.add(prepareSeatInformation(seat, occupiedSeatsList, flight.getPrice()));

        }
        return new DataToReservationDto(flightId, arrivalAirport, arrivalDate, departureAirport, departureDate, seatList);

    }

    private DataToReservationDto.ListOfSeat prepareSeatInformation(Seat seat, List<Long> occupiedSeatsList, Integer price) {
        DataToReservationDto.ListOfSeat element = new DataToReservationDto.ListOfSeat();
        element.setId(seat.getSeatId());
        element.setSeatNumber(seat.getSeatNumber());
        element.setClassType(seat.getClassType().getClassType());
        element.setIsAvailable(true);
        element.setPrice(seat.getClassType().getCalculatePrice() * price);
        if (!occupiedSeatsList.isEmpty()) {
            element.setIsAvailable(true);
        } else {
            if (occupiedSeatsList.contains(seat.getSeatId())) {
                element.setIsAvailable(false);
            }
        }
        return element;
    }
}
