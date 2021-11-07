package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.ReservationStatus;
import lombok.Getter;

@Getter
public class ReservationStatusDto {
    Long reservationId;
    ReservationStatus reservationStatus;
}
