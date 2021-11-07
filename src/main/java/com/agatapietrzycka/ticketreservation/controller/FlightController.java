package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.AirportAndPlaneDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FilterFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightWithFlightStatusesDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.UpdateFlightDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.service.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/ticketreservation/api/flight")
@AllArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public AirportAndPlaneDto getDataToCreateFlight() {
        return flightService.getDataToCreateFlight();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public FlightWithFlightStatusesDto createFlight(@RequestBody CreateOrUpdateFlightDto flightDto) {
        return flightService.createFlight(flightDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<FlightWithFlightStatusesDto> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/allFlight")
    @ResponseStatus(HttpStatus.OK)
    public List<FlightDto> getAllFlightsWithAvailableOrFullStatus() {
        return flightService.getAllAvailableOrFullFlights();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseFlightListDto changeFlightStatus(@RequestBody FlightStatusDto flightStatusDto) {
        return flightService.changeStatus(flightStatusDto);
    }

    @PutMapping("/{flightId}/update")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public FlightWithFlightStatusesDto updateFlight(@PathVariable Long flightId, @RequestBody CreateOrUpdateFlightDto flightUpdateDto) {
        return flightService.updateFlight(flightId, flightUpdateDto);
    }

    @GetMapping("/{flightId}/update")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public UpdateFlightDto showDataToUpdateFlight(@PathVariable Long flightId) {
        return flightService.getDataToUpdate(flightId);
    }

    @GetMapping("/findFlight")
    @ResponseStatus(HttpStatus.OK)
    public List<Flight> filterFlight(@RequestBody FilterFlightDto filterFlightDto) {
        return flightService.getFilterFlight(filterFlightDto);
    }

}
