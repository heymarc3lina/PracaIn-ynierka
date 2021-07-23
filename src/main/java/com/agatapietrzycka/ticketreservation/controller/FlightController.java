package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Plane;
import com.agatapietrzycka.ticketreservation.service.FlightService;
import com.agatapietrzycka.ticketreservation.service.PlaneService;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticketreservation/api/flight")

public class FlightController {

   private final PlaneService planeService;
   private final FlightService flightService;


    @Autowired
    public FlightController(PlaneService planeService, FlightService flightService) {
        this.planeService = planeService;
        this.flightService = flightService;
    }

    @GetMapping("/flight")
    public List<Plane> sayHello() {
        return planeService.getPlain();
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

    //moze to tylko manager lotow
   @PutMapping
    public ResponseEntity<ResponseFlightListDto> changeFlightStatus(@RequestBody FlightStatusDto flightStatusDto){
         ResponseFlightListDto responseFlightListDto = flightService.changeStatus(flightStatusDto);

       return ResponseEntity.status(HttpStatus.OK).body(responseFlightListDto);
    }


}
