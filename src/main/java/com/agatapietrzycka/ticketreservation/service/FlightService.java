package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Airport;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.FlightInformation;
import com.agatapietrzycka.ticketreservation.model.Plain;
import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlainRepository;
import com.agatapietrzycka.ticketreservation.util.exception.TheSameAirportException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (errorMessage.isEmpty()) {
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

    @Transactional(readOnly = true)
    public ResponseDataDto getDataToCreateFlight(){
        List<Plain> plains = plainRepository.findAll();
        List<Airport> airports = airportRepository.findAll();
        return new ResponseDataDto(mapDataToCreateFlight(plains, airports));
    }


    @Transactional(readOnly = true)
    public ResponseFlightListDto getAllFlights(){
        List<Flight> flights = flightRepository.findAll();
        List<ResponseFlightListDto.ListElement> flightListElements = flights.stream()
                .map(this::mapToFlightListElement)
                .collect(Collectors.toList());
        return new ResponseFlightListDto(flightListElements);
    }

    private ResponseDataDto.ListElement mapDataToCreateFlight( List<Plain> plains, List<Airport> airports){
        ResponseDataDto.ListElement listElement = new ResponseDataDto.ListElement();
        listElement.setAirportList(airports);
        listElement.setPlainList(plains);
        return listElement;
    }

    private ResponseFlightListDto.ListElement mapToFlightListElement(Flight flight){
        ResponseFlightListDto.ListElement listElement = new ResponseFlightListDto.ListElement();
        listElement.setId(flight.getId());
        listElement.setArrivalAirports(flight.getArrivalAirport().getCity());
        listElement.setArrivalDate(flight.getArrivalDate());
        listElement.setDepartureAirports(flight.getDepartureAirport().getCity());
        listElement.setDepartureDate(flight.getDepartureDate());
        listElement.setMinPrice(flight.getPrice());
        return listElement;
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

        if(flight.getArrivalAirport().getAirportId() == flight.getDepartureAirport().getAirportId()){
           errorMessages.add(new TheSameAirportException("The same airports are choosen: arrival airport = departure airport.").getMessage());
        }

        Validator validator = validatorFactory.getValidator();
        validator.validate(flight).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }
}
