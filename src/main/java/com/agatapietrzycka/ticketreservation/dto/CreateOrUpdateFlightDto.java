package com.agatapietrzycka.ticketreservation.dto;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateOrUpdateFlightDto {
    private String plain;
    private String arrivalAirport;
    private String departureAirport;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime arrivalDate;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime departureDate;
    private Double price;


}
