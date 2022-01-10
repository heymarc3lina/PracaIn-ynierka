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
        simpleMailMessage.setText("Witamy Cię w naszym serwisie.\n Bardzo się cieszymy, że do nas dołączasz. \n\n" +
                "Kliknij w link w celu aktywacji utworzonego konta: \n" +
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
            seatsList.add("Klasa i numer miejsca: " + s.getSeatNumber() + " " + s.getClassType().getClassType().name() + "\n");
        });
        simpleMailMessage.setText("Dziękujemy za złożenie rezerwacji! \n\n" +
                "Tutaj znajduje się podsumowanie złożonej przez Ciebie rezerwacji: \n" +
                "Czas złożenia rezerwacji: " + reservationDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Imię i nazwisko: " + user.getName() + " " + user.getSurname() + "\n" +
                "Samolot: " + flight.getPlane().getName() + "\n" +
                "Miejsce i czas odlotu: " + flight.getDepartureAirport().getCity() + " " + flight.getDepartureDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Miejsce i czas przylotu: " + flight.getArrivalAirport().getCity() + " " + flight.getArrivalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_TIME)) + "\n" +
                "Cena: " + price + " zł\n" +
                "Miejsca: \n" + String.join("", seatsList) + "\n");
        emailSender.send(simpleMailMessage);
    }
}
