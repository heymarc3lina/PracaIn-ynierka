package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightStatusDto {
    private Long flightId;
    private FlightStatus flightStatus;
}
