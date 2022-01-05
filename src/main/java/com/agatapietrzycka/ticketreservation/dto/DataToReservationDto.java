package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class DataToReservationDto {
    private Long id;
    private String arrivalAirports;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime arrivalDate;
    private String departureAirports;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime departureDate;
    private List<SeatDto> seats;
}
