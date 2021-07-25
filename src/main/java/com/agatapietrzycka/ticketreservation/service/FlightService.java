package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.NewFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Airport;
import com.agatapietrzycka.ticketreservation.model.Flight;
import com.agatapietrzycka.ticketreservation.model.FlightInformation;
import com.agatapietrzycka.ticketreservation.model.Plane;
import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
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
    private final PlaneRepository planeRepository;
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
    public ResponseDataDto getDataToCreateFlight() {
        List<Plane> planes = planeRepository.findAll();
        List<Airport> airports = airportRepository.findAll();
        return new ResponseDataDto(mapDataToCreateFlight(planes, airports));
    }


    @Transactional
    public ResponseFlightListDto getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            if (compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
                flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                flight.getFlightInformation().setUpdatedAt(Instant.now());
            }
        }

        List<ResponseFlightListDto.ListElement> flightListElements = flights.stream()
                .map(this::mapToFlightListElement)
                .collect(Collectors.toList());
        return new ResponseFlightListDto(flightListElements, null);
    }

    @Transactional
    public ResponseFlightListDto getAllAvailableFlights() {
        List<Flight> flights = flightRepository.findAllFlightsAtStatus();
        for (Flight flight : flights) {
            if (compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
                flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                flight.getFlightInformation().setUpdatedAt(Instant.now());
                flights.remove(flight);
            }
        }
        List<ResponseFlightListDto.ListElement> flightListElements = flights.stream()
                .map(this::mapToFlightListElement)
                .collect(Collectors.toList());
        return new ResponseFlightListDto(flightListElements, null);
    }


    @Transactional
    public ResponseFlightListDto changeStatus(FlightStatusDto flightStatusDto) {
        Flight flight = flightRepository.findById(flightStatusDto.getFlightId()).orElseThrow(() -> new CustomFlightException("Flight does not exist!"));

        FlightInformation flightInformation = flight.getFlightInformation();
        List<String> errorMessage = getErrorMessages(flightInformation);
        if (errorMessage.isEmpty()) {
            if (flightStatusDto.getFlightStatus() == FlightStatus.NEW) {
                errorMessage.add("You can't set " + flightStatusDto.getFlightStatus() + ". It has already another status.");
            } else {
                flightInformation.setStatus(flightStatusDto.getFlightStatus());
                flightInformation.setUpdatedAt(Instant.now());
            }
        }

        List<ResponseFlightListDto.ListElement> flightListElements = flightRepository.findAll().stream()
                .map(this::mapToFlightListElement)
                .collect(Collectors.toList());

        return new ResponseFlightListDto(flightListElements, errorMessage);

    }

    private ResponseDataDto.ListElement mapDataToCreateFlight(List<Plane> planes, List<Airport> airports) {
        ResponseDataDto.ListElement listElement = new ResponseDataDto.ListElement();
        listElement.setAirportList(airports);
        listElement.setPlaneList(planes);
        return listElement;
    }

    private ResponseFlightListDto.ListElement mapToFlightListElement(Flight flight) {
        ResponseFlightListDto.ListElement listElement = new ResponseFlightListDto.ListElement();
        listElement.setId(flight.getId());
        listElement.setArrivalAirports(flight.getArrivalAirport().getCity());
        listElement.setArrivalDate(flight.getArrivalDate());
        listElement.setDepartureAirports(flight.getDepartureAirport().getCity());
        listElement.setDepartureDate(flight.getDepartureDate());
        listElement.setMinPrice(flight.getPrice());
        listElement.setFlightStatus(flight.getFlightInformation().getStatus());
        return listElement;
    }

    private Flight mapToFlightEntity(NewFlightDto flightDto) {
        Flight flight = new Flight();
        flight.setArrivalAirport(airportRepository.getById(flightDto.getArrivalAirportId()));
        flight.setDepartureAirport(airportRepository.getById(flightDto.getDepartureAirportId()));
        flight.setPrice(flightDto.getPrice());
        flight.setPlane(planeRepository.getById(flightDto.getPlainId()));
        flight.setArrivalDate(flightDto.getArrivalDate());
        flight.setDepartureDate(flightDto.getDepartureDate());

        return flight;
    }

    private List<String> getErrorMessages(FlightInformation flightInformation) {
        List<String> errorMessages = new ArrayList<>();
        Validator validator = validatorFactory.getValidator();
        validator.validate(flightInformation).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

    private List<String> getErrorMessages(Flight flight) {
        List<String> errorMessages = new ArrayList<>();

        if (flight.getArrivalAirport().getAirportId() == flight.getDepartureAirport().getAirportId()) {
            errorMessages.add("The same airports are choosen: arrival airport = departure airport.");
        }
        if (flight.getArrivalDate() == flight.getDepartureDate() || compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
            errorMessages.add("Dates are the same or are overdue.");
        }

        Validator validator = validatorFactory.getValidator();
        validator.validate(flight).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

    private Boolean compareDate(Instant arrivalDate, Instant departureDate) {
        int compareArrival = arrivalDate.compareTo(Instant.now());
        int compareDeparture = departureDate.compareTo(Instant.now());
        if (compareArrival <= 0 || compareDeparture <= 0) {
            return true;
        }
        return false;
    }
}
