package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.AvailableFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FilterFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDataDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
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
import java.time.LocalDateTime;
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
    public ResponseDto createFlight(CreateOrUpdateFlightDto flightDto) {
        Flight flight = mapToFlightEntity(null, flightDto);
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

    @Transactional
    public ResponseDto updateFlight(Long flightId, CreateOrUpdateFlightDto flightUpdateDto){
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        fillFlight(flight, flightUpdateDto);
        List<String> errorMessage = getErrorMessages(flight);
        ResponseDto responseDto = new ResponseDto(null, errorMessage);
        if(errorMessage.isEmpty()){
            responseDto.setId(flight.getId());
        }
        return responseDto;
    }

    @Transactional(readOnly = true)
    public ResponseDataDto getDataToCreateFlight() {
        List<Plane> planes = planeRepository.findAll();
        List<Airport> airports = airportRepository.findAll();
        return new ResponseDataDto(mapDataToCreateFlight(planes, airports));
    }

   public ResponseFlightListDto getDataToUpdate(Long flightId){
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        ResponseFlightListDto.ListElement flightListElement = mapToFlightListElement(flight);
        return new ResponseFlightListDto(List.of(flightListElement), null);
    }


    @Transactional
    public ResponseFlightListDto getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            if (flight.getFlightInformation().getStatus() != FlightStatus.NEW) {
                if (compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
                    flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                    flight.getFlightInformation().setUpdatedAt(Instant.now());
                }
            }
        }

        List<ResponseFlightListDto.ListElement> flightListElements = flights.stream()
                .map(this::mapToFlightListElement)
                .collect(Collectors.toList());
        return new ResponseFlightListDto(flightListElements, null);
    }

    @Transactional(readOnly = true)
    public AvailableFlightListDto getAllAvailableFlights() {
        List<Flight> flights = flightRepository.findAllFlightsAtStatus();
        overdateingFlights(flights);
        List<AvailableFlightListDto.ListElement> flightListElements = flights.stream()
                .map(this::mapToAvailableFlightListElement)
                .collect(Collectors.toList());
        return new AvailableFlightListDto(flightListElements, null);
    }

    private void overdateingFlights(List<Flight> flights){
        for (Flight flight : flights) {
            if (flight.getFlightInformation().getStatus() != FlightStatus.NEW) {
                if (compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
                    flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                    flight.getFlightInformation().setUpdatedAt(Instant.now());
                    flights.remove(flight);
                }
            }
        }
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

    private AvailableFlightListDto.ListElement mapToAvailableFlightListElement(Flight flight) {
        AvailableFlightListDto.ListElement listElement = new AvailableFlightListDto.ListElement();
        listElement.setId(flight.getId());
        listElement.setArrivalAirports(flight.getArrivalAirport().getCity());
        listElement.setArrivalDate(flight.getArrivalDate());
        listElement.setDepartureAirports(flight.getDepartureAirport().getCity());
        listElement.setDepartureDate(flight.getDepartureDate());
        listElement.setMinPrice(flight.getPrice());
        return listElement;
    }

    private List<FlightStatus> getProperStatusList(Flight flight){
        List<FlightStatus> flightStatuses = null;
        if(flight.getFlightInformation().getStatus() == FlightStatus.AVAILABLE){
            flightStatuses = List.of(FlightStatus.OVERDATE, FlightStatus.CANCELLED);
        }
        else if(flight.getFlightInformation().getStatus() == FlightStatus.NEW){
            flightStatuses = List.of(FlightStatus.AVAILABLE, FlightStatus.CANCELLED);
        }
        else if(flight.getFlightInformation().getStatus() == FlightStatus.OVERDATE){
            flightStatuses = List.of( FlightStatus.CANCELLED);
        }

        return flightStatuses;
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
        listElement.setFlightStatuses(getProperStatusList(flight));
        return listElement;
    }

    private Flight mapToFlightEntity(Flight flight, CreateOrUpdateFlightDto flightDto) {
        if(flight == null) {
            flight = new Flight();
        }

        return fillFlight(flight, flightDto);
    }

    private Flight fillFlight(Flight flight, CreateOrUpdateFlightDto flightDto){
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
            errorMessages.add("The same airports are chosen: arrival airport = departure airport.");
        }
        if (flight.getArrivalDate() == flight.getDepartureDate() || compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
            errorMessages.add("Dates are the same or are overdue.");
        }

        Validator validator = validatorFactory.getValidator();
        validator.validate(flight).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

    private Boolean compareDate(LocalDateTime arrivalDate, LocalDateTime departureDate) {
        int compareArrival = arrivalDate.compareTo(LocalDateTime.now());
        int compareDeparture = departureDate.compareTo(LocalDateTime.now());
        if (compareArrival <= 0 || compareDeparture <= 0) {
            return true;
        }
        return false;
    }

    public List<Flight> getFilterFlight(FilterFlightDto filterFlightDto) {
        List<Flight> flightList = flightRepository.findAllFlightsAtStatus();
        overdateingFlights(flightList);
        List<Flight> filterFlightList = new ArrayList<>();
        for (Flight flight : flightList) {
            if (flight.getArrivalAirport().getCity() == filterFlightDto.getArrivalAirports() &&
                    flight.getDepartureAirport().getCity() == filterFlightDto.getDepartureAirports() &&
                    (flight.getPrice() <= filterFlightDto.getMaxPrice() || flight.getPrice() >= filterFlightDto.getMinPrice())) {
                filterFlightList.add(flight);
            }
        }
        return filterFlightList;
    }
}
