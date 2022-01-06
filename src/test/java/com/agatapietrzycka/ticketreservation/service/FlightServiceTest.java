package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.dto.FlightWithFlightStatusesDto;
import com.agatapietrzycka.ticketreservation.entity.Airport;
import com.agatapietrzycka.ticketreservation.entity.Plane;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FlightServiceTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private PlaneRepository planeRepository;


    private Airport airport1, airport2, airport4;
    private Plane plane;

    @BeforeEach
    public void setUp() {
        Airport airport = new Airport();
        airport.setCity("London");
        airport1 = airportRepository.save(airport);

        Airport airport5 = new Airport();
        airport5.setCity("Warsaw");
        airport4 = airportRepository.save(airport5);

        Plane plane1 = new Plane();
        plane1.setPlace(5);
        plane1.setName("Plane");
        plane = planeRepository.save(plane1);

    }

    @AfterEach
    public void clean() {
        flightRepository.deleteAll();
        airportRepository.deleteAll();
        planeRepository.deleteAll();
    }

    @Test
    public void creatFlightTest() {
        //given
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(6));
        dto.setDepartureAirport(airport4.getCity());
        dto.setDepartureDate(LocalDateTime.now().plusHours(8));
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);

        //when
        FlightWithFlightStatusesDto flight = flightService.createFlight(dto);

        //then

        assertEquals(dto.getArrivalAirport(), flight.getFlightDto().getArrivalAirports());
        assertEquals("NEW", flight.getFlightDto().getFlightStatus().name());
        assertEquals(dto.getArrivalDate(), flight.getFlightDto().getArrivalDate());
        assertEquals(dto.getDepartureAirport(), flight.getFlightDto().getDepartureAirports());
        assertEquals(dto.getDepartureDate(), flight.getFlightDto().getDepartureDate());
        assertEquals(plane.getPlaneId(), flight.getFlightDto().getPlaneId());
        assertEquals(dto.getPrice(), flight.getFlightDto().getMinPrice());

    }


    @Test
    public void creatFlightBadAirportTest() {
        //given
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(6));
        dto.setDepartureAirport(airport1.getCity());
        dto.setDepartureDate(LocalDateTime.now().plusHours(8));
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);

        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.createFlight(dto))
                .withMessage("The same airports are chosen: arrival airport = departure airport.");

    }
}
