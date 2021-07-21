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
public class ResponseDataDto {
    private ResponseDataDto.ListElement data;

    @Getter
    @Setter
    public static final class ListElement {
        List<Airport> airportList;
        List<Plane> planeList;
    }


}
