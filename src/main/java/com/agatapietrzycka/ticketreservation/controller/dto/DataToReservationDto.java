package com.agatapietrzycka.ticketreservation.controller.dto;

import com.agatapietrzycka.ticketreservation.model.enums.ClassType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class DataToReservationDto {
    private Long id;
    private String arrivalAirports;
    private LocalDateTime arrivalDate;
    private String departureAirports;
    private LocalDateTime departureDate;
    private List<ListOfSeat> seats;

    @Getter
    @Setter
    public static final class ListOfSeat {
        private Long id;
        private Integer seatNumber;
        private ClassType classType;
        private Double price;
        private Boolean isAvailable;
    }
}
