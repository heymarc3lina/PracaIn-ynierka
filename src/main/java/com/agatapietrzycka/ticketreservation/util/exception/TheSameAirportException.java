package com.agatapietrzycka.ticketreservation.util.exception;

public class TheSameAirportException extends RuntimeException {
    public TheSameAirportException(String message) {
        super(message);
    }
}
