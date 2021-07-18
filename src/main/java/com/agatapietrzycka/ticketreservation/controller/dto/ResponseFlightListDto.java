package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseFlightListDto {
    private List<ListElement> flights;

    @Getter
    @Setter
    public static final class ListElement {
        private Long id;
        private String arrivalAirports;
        private Instant arrivalDate;
        private String departureAirports;
        private Instant departureDate;
        private Integer minPrice;
    }
}
