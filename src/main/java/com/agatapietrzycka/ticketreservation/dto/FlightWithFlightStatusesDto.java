package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightWithFlightStatusesDto {

    FlightDto flightDto;
    private List<FlightStatus> flightStatuses;

}
