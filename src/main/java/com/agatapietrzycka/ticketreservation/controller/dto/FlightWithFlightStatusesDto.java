package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightWithFlightStatusesDto {

    FlightDto flightDto;
    private List<FlightStatus> flightStatuses;

}
