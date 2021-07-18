package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.Status;
import com.agatapietrzycka.ticketreservation.util.exception.CustomReservationException;

import javax.persistence.AttributeConverter;

public class StatusEnumConverter implements AttributeConverter<Status, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Status attribute) {
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
    public Status convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return Status.SUBMITTED;
            case 2:
                return Status.CANCELED;
            case 3:
                return Status.WAITING;
            default:
                throw new CustomReservationException(dbData + " is not supported!");
        }
    }
}
