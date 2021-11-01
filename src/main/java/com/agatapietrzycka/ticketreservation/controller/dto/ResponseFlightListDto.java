package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseFlightListDto {
    private List<ListElement> flights;
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
        private FlightStatus flightStatus;
        private List<FlightStatus> flightStatuses;
    }
}
