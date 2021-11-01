package com.agatapietrzycka.ticketreservation.controller;


import com.agatapietrzycka.ticketreservation.controller.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ReservationDto;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/createReservation")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CreateReservationDto> createReservation(@RequestBody ReservationDto reservationDto,
                                                                  @AuthenticationPrincipal User user) {
        CreateReservationDto responseDto = reservationService.createReservation(reservationDto, user.getEmail());
        HttpStatus status = responseDto.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(responseDto);

    }

}
