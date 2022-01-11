package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.dto.AirportAndPlaneDto;
import com.agatapietrzycka.ticketreservation.dto.CreateOrUpdateFlightDto;
import com.agatapietrzycka.ticketreservation.dto.FlightDto;
import com.agatapietrzycka.ticketreservation.dto.FlightStatusDto;
import com.agatapietrzycka.ticketreservation.dto.FlightWithFlightStatusesDto;
import com.agatapietrzycka.ticketreservation.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.dto.UpdateFlightDto;
import com.agatapietrzycka.ticketreservation.entity.Airport;
import com.agatapietrzycka.ticketreservation.entity.Flight;
import com.agatapietrzycka.ticketreservation.entity.FlightInformation;
import com.agatapietrzycka.ticketreservation.entity.Plane;
import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


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

    @Autowired
    private FlightInformationRepository flightInformationRepository;

    private Airport airport1, airport4;
    private Plane plane;
    private FlightInformation flightInformation, flightInformation1, flightInformation2, flightInformation3;
    private Flight flight, flight1, flight2, flight3;

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

        FlightInformation newFlightInformation = new FlightInformation();
        newFlightInformation.setUpdatedAt(Instant.now());
        newFlightInformation.setStatus(FlightStatus.AVAILABLE);

        FlightInformation newFlightInformation1 = new FlightInformation();
        newFlightInformation1.setUpdatedAt(Instant.now());
        newFlightInformation1.setStatus(FlightStatus.FULL);

        FlightInformation newFlightInformation2 = new FlightInformation();
        newFlightInformation2.setUpdatedAt(Instant.now());
        newFlightInformation2.setStatus(FlightStatus.OVERDATE);

        FlightInformation newFlightInformation3 = new FlightInformation();
        newFlightInformation3.setUpdatedAt(Instant.now());
        newFlightInformation3.setStatus(FlightStatus.NEW);

        flightInformation = flightInformationRepository.save(newFlightInformation);
        flightInformation1 = flightInformationRepository.save(newFlightInformation1);
        flightInformation2 = flightInformationRepository.save(newFlightInformation2);
        flightInformation3 = flightInformationRepository.save(newFlightInformation3);


        Flight newFlight = new Flight();
        newFlight.setArrivalAirport(airport1);
        newFlight.setArrivalDate(LocalDateTime.now().plusHours(8));
        newFlight.setDepartureAirport(airport4);
        newFlight.setDepartureDate(LocalDateTime.now().plusHours(6));
        newFlight.setFlightInformation(flightInformation);
        newFlight.setPlane(plane);
        newFlight.setPrice(180.0);
        flight = flightRepository.save(newFlight);
        flightInformation.setFlight(flight);

        Flight newFlight1 = new Flight();
        newFlight1.setArrivalAirport(airport1);
        newFlight1.setArrivalDate(LocalDateTime.now().plusHours(8));
        newFlight1.setDepartureAirport(airport4);
        newFlight1.setDepartureDate(LocalDateTime.now().plusHours(6));
        newFlight1.setFlightInformation(flightInformation1);
        newFlight1.setPlane(plane);
        newFlight1.setPrice(180.0);
        flight1 = flightRepository.save(newFlight1);
        flightInformation1.setFlight(flight1);

        Flight newFlight2 = new Flight();
        newFlight2.setArrivalAirport(airport1);
        newFlight2.setArrivalDate(LocalDateTime.now().plusHours(3));
        newFlight2.setDepartureAirport(airport4);
        newFlight2.setDepartureDate(LocalDateTime.now());
        newFlight2.setFlightInformation(flightInformation2);
        newFlight2.setPlane(plane);
        newFlight2.setPrice(180.0);

        flight2 = flightRepository.save(newFlight2);
        flightInformation2.setFlight(flight2);

        Flight newFlight3 = new Flight();
        newFlight3.setArrivalAirport(airport1);
        newFlight3.setArrivalDate(LocalDateTime.now().plusHours(3));
        newFlight3.setDepartureAirport(airport4);
        newFlight3.setDepartureDate(LocalDateTime.now());
        newFlight3.setFlightInformation(flightInformation3);
        newFlight3.setPlane(plane);
        newFlight3.setPrice(180.0);

        flight3 = flightRepository.save(newFlight3);
        flightInformation3.setFlight(flight3);
    }

    @AfterEach
    public void clean() {
        flightRepository.deleteAll();
        flightInformationRepository.deleteAll();
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
        dto.setArrivalDate(LocalDateTime.now().plusHours(8));
        dto.setDepartureAirport(airport1.getCity());
        dto.setDepartureDate(LocalDateTime.now().plusHours(6));
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);

        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.createFlight(dto))
                .withMessage("The same airports are chosen: arrival airport = departure airport.");

    }

    @Test
    public void creatFlightBadDateCase1Test() {
        //given
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(3));
        dto.setDepartureAirport(airport4.getCity());
        dto.setDepartureDate(LocalDateTime.now().plusHours(8));
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);


        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.createFlight(dto))
                .withMessage("Dates are the same or are overdue.");
    }


    @Test
    public void creatFlightBadDateCase2Test() {
        //given
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(6));
        dto.setDepartureDate(LocalDateTime.now().plusHours(6));
        dto.setDepartureAirport(airport4.getCity());

        dto.setPlain(plane.getName());
        dto.setPrice(180.0);


        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.createFlight(dto))
                .withMessage("Dates are the same or are overdue.");

    }

    @Test
    public void creatFlightBadDateCase3Test() {
        //given
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(7));
        dto.setDepartureDate(LocalDateTime.now());
        dto.setDepartureAirport(airport4.getCity());
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);


        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.createFlight(dto))
                .withMessage("Dates are the same or are overdue.");

    }

    @Test
    public void getAllAvailableOrFullFlightsTest() {
        //when:

        List<FlightDto> flights = flightService.getAllAvailableOrFullFlights();
        //then:
        assertEquals(2, flights.size());
        for (FlightDto flightDto : flights) {
            assertNotEquals(FlightStatus.OVERDATE, flightDto.getFlightStatus());
        }

    }

    @Test
    public void getAllFlightsTest() {
        //when:
        List<FlightWithFlightStatusesDto> flights = flightService.getAllFlights();
        //then:
        assertEquals(4, flights.size());
        assertEquals(1, flights.get(0).getFlightStatuses().size());
        assertEquals(1, flights.get(1).getFlightStatuses().size());
        assertEquals(2, flights.get(2).getFlightStatuses().size());
        assertNull(flights.get(3).getFlightStatuses());

    }

    @Test
    public void updateFlightTest() {
        //given:
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport4.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(8));
        dto.setDepartureDate(LocalDateTime.now().plusHours(6));
        dto.setDepartureAirport(airport1.getCity());
        dto.setPlain(plane.getName());
        dto.setPrice(280.0);
        //when:
        FlightWithFlightStatusesDto flightAndStatuses = flightService.updateFlight(flight3.getId(), dto);
        //then

        assertEquals(dto.getArrivalAirport(), flightAndStatuses.getFlightDto().getArrivalAirports());
        assertEquals("NEW", flightAndStatuses.getFlightDto().getFlightStatus().name());
        assertEquals(dto.getArrivalDate(), flightAndStatuses.getFlightDto().getArrivalDate());
        assertEquals(dto.getDepartureAirport(), flightAndStatuses.getFlightDto().getDepartureAirports());
        assertEquals(dto.getDepartureDate(), flightAndStatuses.getFlightDto().getDepartureDate());
        assertEquals(plane.getPlaneId(), flightAndStatuses.getFlightDto().getPlaneId());
        assertEquals(dto.getPrice(), flightAndStatuses.getFlightDto().getMinPrice());
        assertEquals(2, flightAndStatuses.getFlightStatuses().size());
        assertEquals(FlightStatus.AVAILABLE, flightAndStatuses.getFlightStatuses().get(0));
        assertEquals(FlightStatus.CANCELLED, flightAndStatuses.getFlightStatuses().get(1));


    }

    @Test
    public void updateFlightTestCase1() {
        //given:
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(7));
        dto.setDepartureDate(LocalDateTime.now());
        dto.setDepartureAirport(airport4.getCity());
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.updateFlight(flight.getId(), dto))
                .withMessage("Cannot update flight with another status than NEW");


    }

    @Test
    public void updateFlightTestCase2() {
        //given:
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(7));
        dto.setDepartureDate(LocalDateTime.now());
        dto.setDepartureAirport(airport1.getCity());
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.updateFlight(flight3.getId(), dto))
                .withMessage("The same airports are chosen: arrival airport = departure airport.");

    }

    @Test
    public void updateFlightTestCase3() {
        //given:
        CreateOrUpdateFlightDto dto = new CreateOrUpdateFlightDto();
        dto.setArrivalAirport(airport1.getCity());
        dto.setArrivalDate(LocalDateTime.now().plusHours(7));
        dto.setDepartureDate(LocalDateTime.now());
        dto.setDepartureAirport(airport4.getCity());
        dto.setPlain(plane.getName());
        dto.setPrice(180.0);
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.updateFlight(flight3.getId(), dto))
                .withMessage("Dates are the same or are overdue.");

    }

    @Test
    public void getDataToCreateFlight() {
        //given:
        //when:
        AirportAndPlaneDto dto = flightService.getDataToCreateFlight();

        //then:
        assertEquals(2, dto.getAirportList().size());
        assertEquals(1, dto.getPlaneList().size());
    }

    @Test
    public void getDataToUpdateTest() {
        //when:
        UpdateFlightDto updateFlightDto = flightService.getDataToUpdate(flight3.getId());
        //then:
        assertEquals(flight3.getId(), updateFlightDto.getFlightDto().getId());
        assertEquals(flight3.getPlane().getPlaneId(), updateFlightDto.getFlightDto().getPlaneId());
        assertEquals(flight3.getArrivalAirport().getCity(), updateFlightDto.getFlightDto().getArrivalAirports());
        assertEquals(flight3.getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                updateFlightDto.getFlightDto().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(flight3.getDepartureAirport().getCity(), updateFlightDto.getFlightDto().getDepartureAirports());
        assertEquals(flight3.getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                updateFlightDto.getFlightDto().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(flight3.getPrice(), updateFlightDto.getFlightDto().getMinPrice());
        assertEquals(flight3.getFlightInformation().getStatus(), updateFlightDto.getFlightDto().getFlightStatus());
        assertEquals(2, updateFlightDto.getAirportAndPlaneDto().getAirportList().size());
        assertEquals(1, updateFlightDto.getAirportAndPlaneDto().getPlaneList().size());
    }

    @Test
    public void getDataToUpdateCase1Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.getDataToUpdate(flight.getId()))
                .withMessage("Cannot update flight with another status than NEW");

    }

    @Test
    public void changeStatusTest() {
        //given:
        FlightStatusDto dto = new FlightStatusDto();
        dto.setFlightId(flight.getId());
        dto.setFlightStatus(FlightStatus.CANCELLED);
        //when:
        ResponseDto responseDto = flightService.changeStatus(dto);
        //then:
        assertEquals(0, responseDto.getErrorMessage().size());
        assertEquals(flight.getId(), responseDto.getId());
    }

    @Test
    public void changeStatusCase1Test() {
        //given:
        FlightStatusDto dto = new FlightStatusDto();
        dto.setFlightId(flight.getId());
        dto.setFlightStatus(FlightStatus.NEW);
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> flightService.changeStatus(dto))
                .withMessage("You can't set NEW status. It has already another status: AVAILABLE");
    }
}
