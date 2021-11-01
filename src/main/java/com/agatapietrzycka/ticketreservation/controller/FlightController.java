package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.AvailableFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FilterFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.service.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/ticketreservation/api/flight")
@AllArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/data")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ResponseDataDto> getDataToCreateFlight() {
        ResponseDataDto responseDataDto = flightService.getDataToCreateFlight();
        return ResponseEntity.status(HttpStatus.OK).body(responseDataDto);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ResponseDto> createFlight(@RequestBody CreateOrUpdateFlightDto flightDto) {
        ResponseDto responseDto = flightService.createFlight(flightDto);
        HttpStatus status = HttpStatus.OK;
        if (responseDto.getErrorMessage().size() > 0) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(responseDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ResponseFlightListDto> getAllFlights() {
        ResponseFlightListDto allFlights = flightService.getAllFlights();
        return ResponseEntity.status(HttpStatus.OK).body(allFlights);
    }


    @GetMapping("/allFlight")
    public ResponseEntity<AvailableFlightListDto> getAllFlightsWithAvailableStatus() {
        AvailableFlightListDto allFlights = flightService.getAllAvailableFlights();
        return ResponseEntity.status(HttpStatus.OK).body(allFlights);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ResponseFlightListDto> changeFlightStatus(@RequestBody FlightStatusDto flightStatusDto) {
        ResponseFlightListDto responseFlightListDto = flightService.changeStatus(flightStatusDto);
        HttpStatus status = HttpStatus.OK;
        if (responseFlightListDto.getErrorMessage().size() > 0) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(responseFlightListDto);
    }

    @PutMapping("/{flightId}/update")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ResponseDto> updateFlight(@PathVariable Long flightId, @RequestBody CreateOrUpdateFlightDto flightUpdateDto){
        ResponseDto responseDto = flightService.updateFlight(flightId, flightUpdateDto);
        HttpStatus status = HttpStatus.OK;
        if (responseDto.getErrorMessage().size() > 0) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(responseDto);
    }

    @GetMapping("/{flightId}/update")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
     public ResponseEntity<ResponseFlightListDto> showDataToUpdateFlight(@PathVariable Long flightId){
        ResponseFlightListDto responseFlightListDto = flightService.getDataToUpdate(flightId);
        return ResponseEntity.status(HttpStatus.OK).body(responseFlightListDto);
     }

     @GetMapping("/findFlight")
     public List<Flight> filterFlight(@RequestBody FilterFlightDto filterFlightDto){
        return flightService.getFilterFlight(filterFlightDto);
     }

}
