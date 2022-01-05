package com.agatapietrzycka.ticketreservation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateReservationDto {
    Long flightId;
    List<Long> seatList;
}
