package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.util.exception.CustomFlightException;

import javax.persistence.AttributeConverter;

public class FlightStatusEnumConverter implements AttributeConverter<FlightStatus, Integer> {
@Override
public Integer convertToDatabaseColumn(FlightStatus attribute){
    switch (attribute) {
        case NEW:
            return 1;
        case AVAILABLE:
            return 2;
        case OVERDATE:
            return 3;
        case CANCELLED:
            return 4;
        case FULL:
            return 5;
        default:
            throw new CustomFlightException(attribute + " is not supported!");
    }
}

    @Override
    public FlightStatus convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return FlightStatus.NEW;
            case 2:
                return FlightStatus.AVAILABLE;
            case 3:
                return FlightStatus.OVERDATE;
            case 4:
                return FlightStatus.CANCELLED;
            case 5:
                return FlightStatus.FULL;
            default:
                throw new CustomFlightException(dbData + " is not supported!");
        }
    }
}
