package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReservationDto {
    private List<ReservationList> reservationIdList;
    private boolean isSuccess;


    @Getter
    @Setter
    public static final class ReservationList {
        private Long reservationId;
        private boolean success;

    }
}
