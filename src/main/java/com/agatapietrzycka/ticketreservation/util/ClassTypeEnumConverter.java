package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.ClassType;

import javax.persistence.AttributeConverter;

public class ClassTypeEnumConverter implements AttributeConverter<ClassType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ClassType attribute) {
        switch (attribute) {
            case FIRSTCLASS:
                return 1;
            case BUSINESS:
                return 2;
            case ECONOMIC:
                return 3;
            default:
                throw new CustomReservationException(attribute + " is not supported!");
        }
    }

    @Override
    public ClassType convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return ClassType.FIRSTCLASS;
            case 2:
                return ClassType.BUSINESS;
            case 3:
                return ClassType.ECONOMIC;
            default:
                throw new CustomReservationException(dbData + " is not supported!");
        }
    }
}
