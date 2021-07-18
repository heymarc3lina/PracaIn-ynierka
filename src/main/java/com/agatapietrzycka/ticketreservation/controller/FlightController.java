package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Plain;
import com.agatapietrzycka.ticketreservation.service.FlightService;
import com.agatapietrzycka.ticketreservation.service.PlainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticketreservation/api/flight")

public class FlightController {

   private final PlainService plainService;
   private final FlightService flightService;


    @Autowired
    public FlightController(PlainService plainService, FlightService flightService) {
        this.plainService = plainService;
        this.flightService = flightService;
    }

    @GetMapping("/flight")
    public List<Plain> sayHello() {
        return plainService.getPlain();
    }


    @GetMapping("/data")
    //moze to tylko manager lotow
    public ResponseEntity<ResponseDataDto> getDataToCreateFlight(){
        ResponseDataDto responseDataDto = flightService.getDataToCreateFlight();
        return ResponseEntity.status(HttpStatus.OK).body(responseDataDto);
    }


    //moze to tylko manager lotow
    @PostMapping
    public ResponseEntity<ResponseDto> createFlight(@RequestBody NewFlightDto flightDto) {
        ResponseDto responseDto = flightService.createFlight(flightDto);
        HttpStatus status = HttpStatus.OK;
        if (responseDto.getErrorMessage().size() > 0) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(responseDto);
    }

    //wszystkie loty moze widziec tylko manager
    @GetMapping
    public ResponseEntity<ResponseFlightListDto> getAllFlights(){
        ResponseFlightListDto allFlights = flightService.getAllFlights();
        return ResponseEntity.status(HttpStatus.OK).body(allFlights);
    }



}
