package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateReservationDto {
    Long flightId;
    List<Long> seatList;
}
