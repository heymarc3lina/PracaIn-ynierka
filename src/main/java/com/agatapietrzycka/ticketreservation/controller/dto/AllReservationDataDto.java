package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllReservationDataDto {
    String userName;
    String userSurname;
    String userEmail;
    ReservationDto reservationDto;

}
