package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.FlightInformation;
import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final PlainRepository plainRepository;
    private final AirportRepository airportRepository;
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final FlightInformationRepository flightInformationRepository;


    @Transactional
    public ResponseDto createFlight(NewFlightDto flightDto) {
        Flight flight = mapToFlightEntity(flightDto);
        List<String> errorMessage = getErrorMessages(flight);
        ResponseDto responseDto = new ResponseDto(null, errorMessage);
        if(errorMessage.isEmpty()){
            FlightInformation flightInformation = new FlightInformation();
            flightInformation.setFlight(flight);
            flightInformation.setStatus(FlightStatus.NEW);
            flightInformation.setUpdatedAt(Instant.now());
            flight.setFlightInformation(flightInformation);
           flightInformationRepository.save(flightInformation);
           Flight flightManagedEntity = flightRepository.save(flight);
            responseDto.setId(flightManagedEntity.getId());

        }
        return responseDto;
    }

    private Flight mapToFlightEntity(NewFlightDto flightDto){
        Flight flight = new Flight();
        flight.setArrivalAirport(airportRepository.getById(flightDto.getArrivalAirportId()));
        flight.setDepartureAirport(airportRepository.getById(flightDto.getDepartureAirportId()));
        flight.setPrice(flightDto.getPrice());
        flight.setPlain(plainRepository.getById(flightDto.getPlainId()));
        flight.setArrivalDate(flightDto.getArrivalDate());
        flight.setDepartureDate(flightDto.getDepartureDate());

        return flight;
    }

    private List<String> getErrorMessages(Flight flight) {
        List<String> errorMessages = new ArrayList<>();
        Validator validator = validatorFactory.getValidator();
        validator.validate(flight).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }
}
