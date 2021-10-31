package com.agatapietrzycka.ticketreservation.controller;


import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticketreservation/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{flightId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public DataToReservationDto prepareReservationData(@PathVariable Long flightId) {
        return reservationService.prepareDataToReservation(flightId);
    }

}
