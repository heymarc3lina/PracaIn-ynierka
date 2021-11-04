package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FlightDto {

    private Long id;
    private String arrivalAirports;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime arrivalDate;
    private String departureAirports;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime departureDate;
    private Integer minPrice;
    private FlightStatus flightStatus;
    private List<FlightStatus> flightStatuses;

}
