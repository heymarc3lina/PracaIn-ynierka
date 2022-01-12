package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.dto.AllReservationDataDto;
import com.agatapietrzycka.ticketreservation.dto.CreateReservationDto;
import com.agatapietrzycka.ticketreservation.dto.DataToReservationDto;
import com.agatapietrzycka.ticketreservation.dto.ReservationDto;
import com.agatapietrzycka.ticketreservation.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.dto.SummaryCreatedReservationDto;
import com.agatapietrzycka.ticketreservation.entity.Airport;
import com.agatapietrzycka.ticketreservation.entity.Class;
import com.agatapietrzycka.ticketreservation.entity.Flight;
import com.agatapietrzycka.ticketreservation.entity.FlightInformation;
import com.agatapietrzycka.ticketreservation.entity.Plane;
import com.agatapietrzycka.ticketreservation.entity.Reservation;
import com.agatapietrzycka.ticketreservation.entity.ReservationInformation;
import com.agatapietrzycka.ticketreservation.entity.Role;
import com.agatapietrzycka.ticketreservation.entity.Seat;
import com.agatapietrzycka.ticketreservation.entity.User;
import com.agatapietrzycka.ticketreservation.entity.enums.ClassType;
import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.entity.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.entity.enums.RoleType;
import com.agatapietrzycka.ticketreservation.repository.AirportRepository;
import com.agatapietrzycka.ticketreservation.repository.ClassRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.FlightRepository;
import com.agatapietrzycka.ticketreservation.repository.PlaneRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationInformationRepository;
import com.agatapietrzycka.ticketreservation.repository.ReservationRepository;
import com.agatapietrzycka.ticketreservation.repository.RoleRepository;
import com.agatapietrzycka.ticketreservation.repository.SeatRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;
import com.agatapietrzycka.ticketreservation.util.exception.CustomReservationException;
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
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReservationServiceTest {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    ClassRepository classRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private PlaneRepository planeRepository;
    @Autowired
    private FlightInformationRepository flightInformationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationInformationRepository reservationInformationRepository;

    private Airport airport1, airport4;
    private Plane plane;
    private FlightInformation flightInformation, flightInformation1, flightInformation2;
    private Flight flight, flight1, flight2;
    private User user, user1;
    private Reservation reservation, reservation1;
    private Seat seat1, seat2;

    @BeforeEach
    public void setUp() {
        Airport airport = new Airport();
        airport.setCity("London");
        airport1 = airportRepository.save(airport);

        Airport airport5 = new Airport();
        airport5.setCity("Warsaw");
        airport4 = airportRepository.save(airport5);

        Plane plane1 = new Plane();
        plane1.setPlace(2);
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
        newFlightInformation2.setStatus(FlightStatus.AVAILABLE);

        flightInformation = flightInformationRepository.save(newFlightInformation);
        flightInformation1 = flightInformationRepository.save(newFlightInformation1);
        flightInformation2 = flightInformationRepository.save(newFlightInformation2);


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
        newFlight1.setDepartureDate(LocalDateTime.now().minusMinutes(5));
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

        Role newRole = new Role();
        newRole.setRole(RoleType.USER);
        Role role = roleRepository.save(newRole);

        User newUser = new User();
        newUser.setCreatedDate(Instant.now());
        newUser.setActivationDate(LocalDateTime.now());
        newUser.setActive(true);
        newUser.setEmail("user@user.pl");
        newUser.setPassword("12345");
        newUser.setSurname("User");
        newUser.setName("Testowy");
        newUser.setRoles(Set.of(role));
        user = userRepository.save(newUser);

        User newUser1 = new User();
        newUser1.setCreatedDate(Instant.now());
        newUser1.setActivationDate(LocalDateTime.now());
        newUser1.setActive(true);
        newUser1.setEmail("user1@user.pl");
        newUser1.setPassword("12345");
        newUser1.setSurname("User");
        newUser1.setName("Testowy");
        newUser1.setRoles(Set.of(role));
        user1 = userRepository.save(newUser1);

        Class clasType = classRepository.save(new Class(null, 1.0, ClassType.ECONOMIC));

        Seat newSeat2 = new Seat();
        newSeat2.setClassType(clasType);
        newSeat2.setPlane(plane);
        newSeat2.setSeatNumber(2);
        seat2 = seatRepository.save(newSeat2);

        Seat newSeat = new Seat();
        newSeat.setClassType(clasType);
        newSeat.setPlane(plane);
        newSeat.setSeatNumber(1);
        seat1 = seatRepository.save(newSeat);

        ReservationInformation newReservationInformation = new ReservationInformation();
        newReservationInformation.setReservationStatus(ReservationStatus.ACTIVE);
        newReservationInformation.setUpdatedAt(Instant.now());
        ReservationInformation information = reservationInformationRepository.save(newReservationInformation);

        Reservation newReservation = new Reservation();
        newReservation.setFlight(flight);
        newReservation.setReservationDate(LocalDateTime.now());
        newReservation.setReservationInformation(information);
        newReservation.setSeat(seat1);
        newReservation.setUser(user);
        reservation = reservationRepository.save(newReservation);
        information.setReservation(reservation);


        ReservationInformation newReservationInformation1 = new ReservationInformation();
        newReservationInformation1.setReservationStatus(ReservationStatus.ACTIVE);
        newReservationInformation1.setUpdatedAt(Instant.now());
        ReservationInformation information1 = reservationInformationRepository.save(newReservationInformation1);

        Reservation newReservation1 = new Reservation();
        newReservation1.setFlight(flight1);
        newReservation1.setReservationDate(LocalDateTime.now().minusHours(3));
        newReservation1.setReservationInformation(information);
        newReservation1.setSeat(seat1);
        newReservation1.setUser(user1);
        reservation1 = reservationRepository.save(newReservation1);
        information1.setReservation(reservation1);


    }

    @AfterEach
    public void clean() {
        reservationRepository.deleteAll();
        reservationInformationRepository.deleteAll();
        flightRepository.deleteAll();
        flightInformationRepository.deleteAll();
        airportRepository.deleteAll();
        seatRepository.deleteAll();
        planeRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

    }

    @Test
    public void prepareDataToReservationTest() {
        //given:
        //when:
        DataToReservationDto dto = reservationService.prepareDataToReservation(flight.getId());

        //than:
        assertEquals(flight.getArrivalAirport().getCity(), dto.getArrivalAirports());
        assertEquals(flight.getDepartureAirport().getCity(), dto.getDepartureAirports());
        assertEquals(flight.getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dto.getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(flight.getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dto.getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(flight.getId(), dto.getId());
        assertEquals(2, dto.getSeats().size());
        assertEquals(true, dto.getSeats().get(0).getIsAvailable());
        assertEquals(false, dto.getSeats().get(1).getIsAvailable());

    }

    @Test
    public void prepareDataToReservationCase1Test() {
        //given:
        //when:
        //than:
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> reservationService.prepareDataToReservation(5L))
                .withMessage("No value present");
    }

    @Test
    public void createReservationTest() {
        //given:
        CreateReservationDto dto = new CreateReservationDto();
        dto.setFlightId(flight.getId());
        dto.setSeatList(List.of(seat1.getSeatId()));
        //when:
        SummaryCreatedReservationDto createdReservationDto = reservationService.createReservation(dto, user.getEmail());
        //than:
        assertEquals(1, createdReservationDto.getReservationIdList().size());
        assertTrue(createdReservationDto.getReservationIdList().get(0).isSuccess());
    }

    @Test
    public void createReservationCase1Test() {
        //given:
        CreateReservationDto dto = new CreateReservationDto();
        dto.setFlightId(flight.getId());
        dto.setSeatList(List.of(seat1.getSeatId()));
        //when:
        //then:
        assertThatExceptionOfType(CustomReservationException.class)
                .isThrownBy(() -> reservationService.createReservation(dto, "userEmail@user.pl"))
                .withMessage("User does not exist");
    }

    @Test
    public void createReservationCase2Test() {
        //given:
        CreateReservationDto dto = new CreateReservationDto();
        dto.setFlightId(5L);
        dto.setSeatList(List.of(seat1.getSeatId()));
        //when:
        //then:
        assertThatExceptionOfType(CustomFlightException.class)
                .isThrownBy(() -> reservationService.createReservation(dto, user.getEmail()))
                .withMessage("Flight does not exist");
    }

    @Test
    public void getAllReservationsTest() {
        //given:
        //when:
        List<AllReservationDataDto> dtos = reservationService.getAllReservations();
        //then:
        assertEquals(2, dtos.size());
        assertEquals(reservation.getUser().getEmail(), dtos.get(0).getUserEmail());
        assertEquals(reservation.getUser().getName(), dtos.get(0).getUserName());
        assertEquals(reservation.getUser().getSurname(), dtos.get(0).getUserSurname());
        assertEquals(reservation.getFlight().getArrivalAirport().getCity(), dtos.get(0).getReservationDto().getArrivalAirport());
        assertEquals(reservation.getFlight().getDepartureAirport().getCity(), dtos.get(0).getReservationDto().getDepartureAirport());
        assertEquals(reservation.getReservationId(), dtos.get(0).getReservationDto().getReservationId());
        assertEquals(reservation.getFlight().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getReservationDto().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getFlight().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getReservationDto().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getFlight().getPlane().getName(), dtos.get(0).getReservationDto().getPlaneName());
        assertEquals(180.0, dtos.get(0).getReservationDto().getPrice());
        assertEquals(reservation.getSeat().getSeatNumber(), dtos.get(0).getReservationDto().getSeatNumber());
        assertEquals(reservation.getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getReservationDto().getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getReservationInformation().getReservationStatus(), dtos.get(0).getReservationDto().getReservationStatus());

        assertEquals(reservation1.getUser().getEmail(), dtos.get(1).getUserEmail());
        assertEquals(reservation1.getUser().getName(), dtos.get(1).getUserName());
        assertEquals(reservation1.getUser().getSurname(), dtos.get(1).getUserSurname());
        assertEquals(reservation1.getFlight().getArrivalAirport().getCity(), dtos.get(1).getReservationDto().getArrivalAirport());
        assertEquals(reservation1.getFlight().getDepartureAirport().getCity(), dtos.get(1).getReservationDto().getDepartureAirport());
        assertEquals(reservation1.getReservationId(), dtos.get(1).getReservationDto().getReservationId());
        assertEquals(reservation1.getFlight().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(1).getReservationDto().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation1.getFlight().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(1).getReservationDto().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation1.getFlight().getPlane().getName(), dtos.get(1).getReservationDto().getPlaneName());
        assertEquals(180.0, dtos.get(1).getReservationDto().getPrice());
        assertEquals(reservation1.getSeat().getSeatNumber(), dtos.get(1).getReservationDto().getSeatNumber());
        assertEquals(reservation1.getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(1).getReservationDto().getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(ReservationStatus.COMPLETED, dtos.get(1).getReservationDto().getReservationStatus());

    }

    @Test
    public void getAllUserReservationTest() {
        //given:
        //when:
        List<ReservationDto> dtos = reservationService.getAllUserReservation(user.getEmail());
        //then:
        assertEquals(1, dtos.size());
        assertEquals(reservation.getFlight().getArrivalAirport().getCity(), dtos.get(0).getArrivalAirport());
        assertEquals(reservation.getFlight().getDepartureAirport().getCity(), dtos.get(0).getDepartureAirport());
        assertEquals(reservation.getReservationId(), dtos.get(0).getReservationId());
        assertEquals(reservation.getFlight().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getFlight().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getFlight().getPlane().getName(), dtos.get(0).getPlaneName());
        assertEquals(180.0, dtos.get(0).getPrice());
        assertEquals(reservation.getSeat().getSeatNumber(), dtos.get(0).getSeatNumber());
        assertEquals(reservation.getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation.getReservationInformation().getReservationStatus(), dtos.get(0).getReservationStatus());

    }

    @Test
    public void getAllUserReservationCase1Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(CustomReservationException.class)
                .isThrownBy(() -> reservationService.getAllUserReservation("badEmail@gmail.com"))
                .withMessage("User does not exist");
    }

    @Test
    public void getAllUserReservationCase2Test() {
        //given:
        //when:
        List<ReservationDto> dtos = reservationService.getAllUserReservation(user1.getEmail());
        //then:
        assertEquals(1, dtos.size());
        assertEquals(reservation1.getFlight().getArrivalAirport().getCity(), dtos.get(0).getArrivalAirport());
        assertEquals(reservation1.getFlight().getDepartureAirport().getCity(), dtos.get(0).getDepartureAirport());
        assertEquals(reservation1.getReservationId(), dtos.get(0).getReservationId());
        assertEquals(reservation1.getFlight().getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getDepartureDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation1.getFlight().getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getArrivalDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(reservation1.getFlight().getPlane().getName(), dtos.get(0).getPlaneName());
        assertEquals(180.0, dtos.get(0).getPrice());
        assertEquals(reservation1.getSeat().getSeatNumber(), dtos.get(0).getSeatNumber());
        assertEquals(reservation1.getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                dtos.get(0).getReservationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(ReservationStatus.COMPLETED, dtos.get(0).getReservationStatus());

    }

    @Test
    public void cancelReservationTest() {
        //given:
        //when:
        ResponseDto dto = reservationService.cancelReservation(reservation.getReservationId());
        //then:
        assertEquals(reservation.getReservationId(), dto.getId());
        assertEquals(0, dto.getErrorMessage().size());
        assertEquals(ReservationStatus.CANCELED, reservationRepository.findById(reservation.getReservationId()).get().getReservationInformation().getReservationStatus());
    }

    @Test
    public void cancelReservationTestCase1Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(CustomReservationException.class)
                .isThrownBy(() -> reservationService.cancelReservation(5L))
                .withMessage("Reservation does not exist!");

    }


}
