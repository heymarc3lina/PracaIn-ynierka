package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;

import javax.persistence.AttributeConverter;

public class FlightStatusEnumConverter implements AttributeConverter<FlightStatus, Integer> {
@Override
public Integer convertToDatabaseColumn(FlightStatus attribute){
    switch (attribute) {
        case NEW:
            return 1;
        case SUBBMITTED:
            return 2;
        case OVERDATE:
            return 3;
        case CANCELLED:
            return 4;
        case CLOSED:
            return 5;
        default:
            throw new CustomReservationException(attribute + " is not supported!");
    }
}

    @Override
    public FlightStatus convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return FlightStatus.NEW;
            case 2:
                return FlightStatus.SUBBMITTED;
            case 3:
                return FlightStatus.OVERDATE;
            case 4:
                return FlightStatus.CANCELLED;
            case 5:
                return FlightStatus.CLOSED;
            default:
                throw new CustomReservationException(dbData + " is not supported!");
        }
    }
}
