package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AvailableFlightListDto {
    private List<AvailableFlightListDto.ListElement> flights;
    private List<String> errorMessage;

    @Getter
    @Setter
    public static final class ListElement {
        private Long id;
        private String arrivalAirports;
        private LocalDateTime arrivalDate;
        private String departureAirports;
        private LocalDateTime departureDate;
        private Integer minPrice;
    }
}
