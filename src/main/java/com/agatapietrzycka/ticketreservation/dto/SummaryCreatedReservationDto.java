package com.agatapietrzycka.ticketreservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryCreatedReservationDto {
    private List<CreatedReservationDto> reservationIdList;
    private boolean isSuccess;

}
