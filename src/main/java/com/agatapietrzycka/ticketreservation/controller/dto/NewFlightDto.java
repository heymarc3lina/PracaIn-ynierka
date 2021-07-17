package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.Plain;
import lombok.Getter;

import java.time.Instant;

@Getter
public class NewFlightDto {
    private Long plainId;
    private Long arrivalAirportId;
    private Long departureAirportId;
    private Instant arrivalDate;
    private Instant departureDate;
    private Integer price;



}
