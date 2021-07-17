package com.agatapietrzycka.ticketreservation.util;

import com.agatapietrzycka.ticketreservation.model.enums.RoleType;

import javax.persistence.AttributeConverter;

public class RoleEnumConverter  implements AttributeConverter<RoleType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleType attribute) {
        switch (attribute) {
            case ADMIN:
                return 1;
            case MANAGER:
                return 2;
            case USER:
                return 3;
            default:
                throw new CustomReservationException(attribute + " is not supported!");
        }
    }

    @Override
    public RoleType convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1:
                return RoleType.ADMIN;
            case 2:
                return RoleType.MANAGER;
            case 3:
                return RoleType.USER;
            default:
                throw new CustomReservationException(dbData + " is not supported!");
        }
    }
}
