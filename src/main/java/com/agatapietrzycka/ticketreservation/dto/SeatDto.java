package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.entity.enums.ClassType;
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
