package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.ReservationStatus;
import com.agatapietrzycka.ticketreservation.util.exception.CustomReservationException;

import javax.persistence.AttributeConverter;

public class StatusEnumConverter implements AttributeConverter<ReservationStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReservationStatus attribute) {
        switch (attribute) {
            case SUBMITTED:
                return 1;
            case CANCELED:
                return 2;
            case WAITING:
            default:
                throw new CustomReservationException(attribute + " is not supported!");
        }
    }

    @Override
    public ReservationStatus convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return ReservationStatus.SUBMITTED;
            case 2:
                return ReservationStatus.CANCELED;
            case 3:
                return ReservationStatus.WAITING;
            default:
                throw new CustomReservationException(dbData + " is not supported!");
        }
    }
}
