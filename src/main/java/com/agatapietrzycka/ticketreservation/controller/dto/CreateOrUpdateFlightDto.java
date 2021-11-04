package com.agatapietrzycka.ticketreservation.controller.dto;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateOrUpdateFlightDto {
    private Long plainId;
    private Long arrivalAirportId;
    private Long departureAirportId;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime arrivalDate;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime departureDate;
    private Integer price;


}
