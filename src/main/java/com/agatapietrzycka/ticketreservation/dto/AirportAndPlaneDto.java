package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.Airport;
import com.agatapietrzycka.ticketreservation.entity.Plane;
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
