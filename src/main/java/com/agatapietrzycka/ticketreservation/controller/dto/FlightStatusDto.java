package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import lombok.Getter;

@Getter
public class FlightStatusDto {
    private Long flightId;
    private FlightStatus flightStatus;
}
