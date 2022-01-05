package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.dto.AirportAndPlaneDto;
import com.agatapietrzycka.ticketreservation.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.dto.FilterFlightDto;
import com.agatapietrzycka.ticketreservation.dto.FlightDto;
import com.agatapietrzycka.ticketreservation.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.dto.FlightWithFlightStatusesDto;
import com.agatapietrzycka.ticketreservation.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.dto.UpdateFlightDto;
import com.agatapietrzycka.ticketreservation.entity.Airport;
import com.agatapietrzycka.ticketreservation.entity.Flight;
import com.agatapietrzycka.ticketreservation.entity.FlightInformation;
import com.agatapietrzycka.ticketreservation.entity.Plane;
import com.agatapietrzycka.ticketreservation.entity.Reservation;
import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.entity.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final ReservationRepository reservationRepository;


    @Transactional
    public FlightWithFlightStatusesDto createFlight(CreateOrUpdateFlightDto createOrUpdateFlightDto) {
        Flight flight = mapToFlightEntity(null, createOrUpdateFlightDto);
        validateFlight(flight);
        FlightInformation flightInformation = new FlightInformation();
        flightInformation.setFlight(flight);
        flightInformation.setStatus(FlightStatus.NEW);
        flightInformation.setUpdatedAt(Instant.now());
        flight.setFlightInformation(flightInformation);
        flightInformationRepository.save(flightInformation);
        Flight flightManagedEntity = flightRepository.save(flight);
        FlightWithFlightStatusesDto flightDto = mapToFlightWithFlightStatusesDto(flightManagedEntity);
        return flightDto;
    }

    @Transactional
    public FlightWithFlightStatusesDto updateFlight(Long flightId, CreateOrUpdateFlightDto flightUpdateDto) {
        Flight flight = flightRepository.findById(flightId).orElseThrow();
        fillFlight(flight, flightUpdateDto);
        validateFlight(flight);
        return mapToFlightWithFlightStatusesDto(flight);
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
    public List<FlightWithFlightStatusesDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAll().stream()
                .sorted(Comparator.comparing(Flight::getDepartureDate).reversed())
                .collect(Collectors.toList());
        ;
        for (Flight flight : flights) {
            if (flight.getFlightInformation().getStatus() == FlightStatus.AVAILABLE || flight.getFlightInformation().getStatus() == FlightStatus.FULL) {
                if (compareDate(flight.getArrivalDate(), flight.getDepartureDate(), false)) {
                    flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                    flight.getFlightInformation().setUpdatedAt(Instant.now());
                }
            }
        }
        return flights.stream()
                .map(this::mapToFlightWithFlightStatusesDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlightDto> getAllAvailableOrFullFlights() {
        List<Flight> flights = flightRepository.findAllFlightsAtStatus();
        overdateingFlights(flights);
        return flights.stream()
                .map(this::mapToFlightDto)
                .collect(Collectors.toList());
    }

    private void overdateingFlights(List<Flight> flights){
        for (Flight flight : flights) {
            FlightStatus flightStatus = flight.getFlightInformation().getStatus();
            if (flightStatus != FlightStatus.NEW) {
                if (compareDate(flight.getArrivalDate(), flight.getDepartureDate(), false)) {
                    flight.getFlightInformation().setStatus(FlightStatus.OVERDATE);
                    flight.getFlightInformation().setUpdatedAt(Instant.now());
                    flights.remove(flight);
                }
            }
        }
    }

    @Transactional
    public ResponseDto changeStatus(FlightStatusDto flightStatusDto) {
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
        if (flightStatusDto.getFlightStatus() == FlightStatus.CANCELLED) {
            List<Reservation> reservation = reservationRepository.findAllByFlightId(flight.getId());
            reservation.forEach(r -> {
                r.getReservationInformation().setReservationStatus(ReservationStatus.CANCELED);
                r.getReservationInformation().setUpdatedAt(Instant.now());
            });
        }
        return new ResponseDto(flight.getId(), errorMessage);
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
        flightDto.setPlaneId(flight.getPlane().getPlaneId());
        return flightDto;
    }

    private List<FlightStatus> getProperStatusList(Flight flight) {
        List<FlightStatus> flightStatuses = null;
        FlightStatus flightStatus = flight.getFlightInformation().getStatus();

        if (flightStatus == FlightStatus.AVAILABLE || flightStatus == FlightStatus.FULL) {
            flightStatuses = List.of(FlightStatus.CANCELLED);
        } else if (flightStatus == FlightStatus.NEW) {
            flightStatuses = List.of(FlightStatus.AVAILABLE, FlightStatus.CANCELLED);
        } else if (flightStatus == FlightStatus.OVERDATE) {
            flightStatuses = List.of(FlightStatus.CANCELLED);
        }

        return flightStatuses;
    }

    private FlightWithFlightStatusesDto mapToFlightWithFlightStatusesDto(Flight flight) {
        FlightWithFlightStatusesDto flightWithFlightStatusesDto = new FlightWithFlightStatusesDto();
        FlightDto flightDto = mapToFlightDto(flight);
        flightWithFlightStatusesDto.setFlightDto(flightDto);
        flightWithFlightStatusesDto.setFlightStatuses(getProperStatusList(flight));
        return flightWithFlightStatusesDto;
    }

    private Flight mapToFlightEntity(Flight flight, CreateOrUpdateFlightDto flightDto) {
        if (flight == null) {
            flight = new Flight();
        }

        return fillFlight(flight, flightDto);
    }

    private Flight fillFlight(Flight flight, CreateOrUpdateFlightDto flightDto) {
        flight.setArrivalAirport(airportRepository.getByCity(flightDto.getArrivalAirport()));
        flight.setDepartureAirport(airportRepository.getByCity(flightDto.getDepartureAirport()));
        flight.setPrice(flightDto.getPrice());
        flight.setPlane(planeRepository.getByName(flightDto.getPlain()));
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
        if (flight.getArrivalDate() == flight.getDepartureDate() || compareDate(flight.getArrivalDate(), flight.getDepartureDate(), true)) {
            throw new CustomFlightException("Dates are the same or are overdue.");
        }

        Validator validator = validatorFactory.getValidator();
        validator.validate(flight);
    }

    private Boolean compareDate(LocalDateTime arrivalDate, LocalDateTime departureDate, boolean creatingFlight) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("UTC"));
        if (creatingFlight) {
            currentTime.plusHours(5);
        } else {
            currentTime.plusHours(3);
        }
        int compareArrival = arrivalDate.compareTo(currentTime);
        int compareDeparture = departureDate.compareTo(currentTime);
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
