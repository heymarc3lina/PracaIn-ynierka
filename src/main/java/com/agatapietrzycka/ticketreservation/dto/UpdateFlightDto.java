package com.agatapietrzycka.ticketreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFlightDto {
    FlightDto flightDto;
    AirportAndPlaneDto airportAndPlaneDto;

}
