package com.agatapietrzycka.ticketreservation.controller;


import com.agatapietrzycka.ticketreservation.controller.dto.AllReservationDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.SummaryCreatedReservationDto;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticketreservation/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{flightId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public DataToReservationDto prepareReservationData(@PathVariable Long flightId) {
        return reservationService.prepareDataToReservation(flightId);
    }

    @PostMapping("/createReservation")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public SummaryCreatedReservationDto createReservation(@RequestBody CreateReservationDto createReservationDto,
                                                          @AuthenticationPrincipal User user) {
        return reservationService.createReservation(createReservationDto, user.getEmail());

    }

    @GetMapping("/allReservationForUsers")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public List<AllReservationDataDto> getAllReservationForUsers() {
        return reservationService.getAllReservationForUsers();
    }

    @GetMapping("/allMyReservation")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ReservationDto> getAllUserReservation(@AuthenticationPrincipal User user) {
        return reservationService.getAllUserReservation(user.getEmail());
    }
}
