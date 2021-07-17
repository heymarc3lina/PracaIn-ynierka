package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
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
    public List<Plain> sayHello(){
        return plainService.getPlain();
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createFlight(@RequestBody NewFlightDto flightDto){
        ResponseDto responseDto = flightService.createFlight(flightDto);
        HttpStatus ststus = HttpStatus.OK;
        if(responseDto.getErrorMessage().size() > 0){
            ststus = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(ststus).body(responseDto);
    }


}
