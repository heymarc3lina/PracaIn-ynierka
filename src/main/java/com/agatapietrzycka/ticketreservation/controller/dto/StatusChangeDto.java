package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
public class StatusChangeDto {
    private FlightStatus flightStatus;
    private Long id;


}