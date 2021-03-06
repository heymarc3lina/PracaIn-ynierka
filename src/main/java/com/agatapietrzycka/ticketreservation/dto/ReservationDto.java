package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDto {
    Long reservationId;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    LocalDateTime reservationDate;
    String planeName;
    String arrivalAirport;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    LocalDateTime arrivalDate;
    String departureAirport;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    LocalDateTime departureDate;
    Integer seatNumber;
    ReservationStatus reservationStatus;
    Double price;
}
