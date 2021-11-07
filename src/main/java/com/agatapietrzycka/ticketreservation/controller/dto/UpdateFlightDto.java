package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFlightDto {
    FlightWithFlightStatusesDto flightDto;
    AirportAndPlaneDto airportAndPlaneDto;

}
