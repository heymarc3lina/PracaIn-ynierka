package com.agatapietrzycka.ticketreservation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedReservationDto {
    private Long reservationId;
    private boolean success;
}
