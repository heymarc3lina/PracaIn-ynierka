package com.agatapietrzycka.ticketreservation.dto;

import lombok.Getter;



@Getter
public class FilterFlightDto {
    private String arrivalAirports;
    private String departureAirports;
    private Integer minPrice;
    private Integer maxPrice;

}
