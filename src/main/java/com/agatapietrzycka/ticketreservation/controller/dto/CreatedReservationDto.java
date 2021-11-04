package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedReservationDto {
    private Long reservationId;
    private boolean success;
}
