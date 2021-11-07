package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseFlightListDto {
    private List<FlightWithFlightStatusesDto> flights;
    private List<String> errorMessage;
}
