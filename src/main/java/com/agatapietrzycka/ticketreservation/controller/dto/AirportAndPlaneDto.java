package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.Airport;
import com.agatapietrzycka.ticketreservation.model.Plane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AirportAndPlaneDto {
    List<Airport> airportList;
    List<Plane> planeList;
}
