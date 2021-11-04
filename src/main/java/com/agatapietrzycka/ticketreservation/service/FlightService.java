package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.AirportAndPlaneDto;
import com.agatapietrzycka.ticketreservation.controller.dto.AvailableFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FilterFlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightDto;
import com.agatapietrzycka.ticketreservation.controller.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseFlightListDto;
import com.agatapietrzycka.ticketreservation.controller.dto.UpdateFlightDto;
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
    public FlightDto createFlight(CreateOrUpdateFlightDto createOrUpdateFlightDto) {
        Flight flight = mapToFlightEntity(null, createOrUpdateFlightDto);
        validateFlight(flight);
        FlightDto flightDto = new FlightDto();
        FlightInformation flightInformation = new FlightInformation();
        flightInformation.setFlight(flight);
        flightInformation.setStatus(FlightStatus.NEW);
        flightInformation.setUpdatedAt(Instant.now());
        flight.setFlightInformation(flightInformation);
        flightInformationRepository.save(flightInformation);
        Flight flightManagedEntity = flightRepository.save(flight);
        flightDto.setId(flightManagedEntity.getId());
        return flightDto;
    }

    @Transactional
    public FlightDto updateFlight(Long flightId, CreateOrUpdateFlightDto flightUpdateDto) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        fillFlight(flight, flightUpdateDto);
        validateFlight(flight);
        return mapToFlightDto(flight);
    }

    @Transactional(readOnly = true)
    public AirportAndPlaneDto getDataToCreateFlight() {
        List<Plane> planes = planeRepository.findAll();
        List<Airport> airports = airportRepository.findAll();
        return new AirportAndPlaneDto(airports, planes);
    }

    public UpdateFlightDto getDataToUpdate(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        FlightDto flightDto = mapToFlightDto(flight);
        AirportAndPlaneDto airportAndPlaneDto = new AirportAndPlaneDto(airportRepository.findAll(), planeRepository.findAll());
        return new UpdateFlightDto(flightDto, airportAndPlaneDto);
    }


    @Transactional
    public List<FlightDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            if (flight.getFlightInformation().getStatus() != FlightStatus.NEW) {
                if (compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
                    flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                    flight.getFlightInformation().setUpdatedAt(Instant.now());
                }
            }
        }
        return flights.stream()
                .map(this::mapToFlightDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvailableFlightDto> getAllAvailableFlights() {
        List<Flight> flights = flightRepository.findAllFlightsAtStatus();
        overdateingFlights(flights);
        return flights.stream()
                .map(this::mapToAvailableFlightListElement)
                .collect(Collectors.toList());
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
        List<String> errorMessage = validateFlight(flightInformation);
        if (errorMessage.isEmpty()) {
            if (flightStatusDto.getFlightStatus() == FlightStatus.NEW) {
                throw new CustomFlightException("You can't set " + flightStatusDto.getFlightStatus() + " status. It has already another status: . " + flightInformation.getStatus());
            } else {
                flightInformation.setStatus(flightStatusDto.getFlightStatus());
                flightInformation.setUpdatedAt(Instant.now());
            }
        }
        List<FlightDto> flightListElements = flightRepository.findAll().stream()
                .map(this::mapToFlightDto)
                .collect(Collectors.toList());

        return new ResponseFlightListDto(flightListElements, errorMessage);

    }

    private AvailableFlightDto mapToAvailableFlightListElement(Flight flight) {
        AvailableFlightDto availableFlightDto = new AvailableFlightDto();
        availableFlightDto.setId(flight.getId());
        availableFlightDto.setArrivalAirports(flight.getArrivalAirport().getCity());
        availableFlightDto.setArrivalDate(flight.getArrivalDate());
        availableFlightDto.setDepartureAirports(flight.getDepartureAirport().getCity());
        availableFlightDto.setDepartureDate(flight.getDepartureDate());
        availableFlightDto.setMinPrice(flight.getPrice());
        return availableFlightDto;
    }

    private List<FlightStatus> getProperStatusList(Flight flight) {
        List<FlightStatus> flightStatuses = null;
        if (flight.getFlightInformation().getStatus() == FlightStatus.AVAILABLE) {
            flightStatuses = List.of(FlightStatus.OVERDATE, FlightStatus.CANCELLED);
        } else if (flight.getFlightInformation().getStatus() == FlightStatus.NEW) {
            flightStatuses = List.of(FlightStatus.AVAILABLE, FlightStatus.CANCELLED);
        } else if (flight.getFlightInformation().getStatus() == FlightStatus.OVERDATE) {
            flightStatuses = List.of(FlightStatus.CANCELLED);
        }

        return flightStatuses;
    }

    private FlightDto mapToFlightDto(Flight flight) {
        FlightDto flightDto = new FlightDto();
        flightDto.setId(flight.getId());
        flightDto.setArrivalAirports(flight.getArrivalAirport().getCity());
        flightDto.setArrivalDate(flight.getArrivalDate());
        flightDto.setDepartureAirports(flight.getDepartureAirport().getCity());
        flightDto.setDepartureDate(flight.getDepartureDate());
        flightDto.setMinPrice(flight.getPrice());
        flightDto.setFlightStatus(flight.getFlightInformation().getStatus());
        flightDto.setFlightStatuses(getProperStatusList(flight));
        return flightDto;
    }

    private Flight mapToFlightEntity(Flight flight, CreateOrUpdateFlightDto flightDto) {
        if (flight == null) {
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

    private List<String> validateFlight(FlightInformation flightInformation) {
        List<String> errorMessages = new ArrayList<>();
        Validator validator = validatorFactory.getValidator();
        validator.validate(flightInformation).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

    private void validateFlight(Flight flight) {

        if (flight.getArrivalAirport().getAirportId() == flight.getDepartureAirport().getAirportId()) {
            throw new CustomFlightException("The same airports are chosen: arrival airport = departure airport.");
        }
        if (flight.getArrivalDate() == flight.getDepartureDate() || compareDate(flight.getArrivalDate(), flight.getDepartureDate())) {
            throw new CustomFlightException("Dates are the same or are overdue.");
        }

        Validator validator = validatorFactory.getValidator();
        validator.validate(flight);
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
