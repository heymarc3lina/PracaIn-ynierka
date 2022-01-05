package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.entity.Flight;
import com.agatapietrzycka.ticketreservation.entity.Seat;
import com.agatapietrzycka.ticketreservation.entity.Token;
import com.agatapietrzycka.ticketreservation.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.agatapietrzycka.ticketreservation.validation.ApplicationConstants.DATE_FORMAT_WITH_TIME;

@Component
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    @Async
    public void sendAccountActivationEmail(Token token) {
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("noreply@reservation-service.com");
        simpleMailMessage.setTo(token.getUser().getEmail());
        simpleMailMessage.setSubject("Reservation service - Account activation");
        simpleMailMessage.setText("Click link to activate an account: \n" +
                "http://localhost:8081/ticketreservation/api/register/activate/" + token.getToken());
        emailSender.send(simpleMailMessage);
    }

    @Async
    public void sendReservationSumary(Flight flight, List<Seat> seats, User user, LocalDateTime reservationDate, double price) {
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("noreply@reservation-service.com");
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Reservation service - Reservation Summary");
        List<String> seatsList = new ArrayList<>();
        seats.forEach(s -> {
            seatsList.add("Seat number and class: " + s.getSeatNumber() + " " + s.getClassType().getClassType().name() + "\n");
        });
        simpleMailMessage.setText("Thanks for your reservation! \n" +
                "Here is your reservation summary: \n" +
                "Reservation time: " + reservationDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Name and surname: " + user.getName() + " " + user.getSurname() + "\n " +
                "Plane: " + flight.getPlane().getName() + "\n" +
                "Departure airport and date: " + flight.getDepartureAirport().getCity() + " " + flight.getDepartureDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Arrival airport and date: " + flight.getArrivalAirport().getCity() + " " + flight.getArrivalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Seats: \n" + String.join("", seatsList) + "\n" +
                "Price: " + price + " pln\n");
        emailSender.send(simpleMailMessage);
    }
}
