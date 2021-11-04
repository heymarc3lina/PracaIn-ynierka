package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.ClassType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDto {
    private Long id;
    private Integer seatNumber;
    private ClassType classType;
    private Double price;
    private Boolean isAvailable;
}
