package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationDto {
    Long flightId;
    List<SeatList> seatList;

    @Getter
    public static final class SeatList {
        Long seatId;
    }

}
