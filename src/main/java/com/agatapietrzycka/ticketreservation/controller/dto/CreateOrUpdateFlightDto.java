package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateOrUpdateFlightDto {
    private Long plainId;
    private Long arrivalAirportId;
    private Long departureAirportId;
    private LocalDateTime arrivalDate;
    private LocalDateTime departureDate;
    private Integer price;


}
