package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import lombok.Getter;

@Getter
public class FlightStatusDto {
    private Long flightId;
    private FlightStatus flightStatus;
}
