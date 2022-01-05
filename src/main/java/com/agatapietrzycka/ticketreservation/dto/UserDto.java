package com.agatapietrzycka.ticketreservation.dto;

import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserDto {
    Long id;
    String name;
    String surname;
    String email;
    String role;
    Boolean isActive;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime createdDate;
    @JsonFormat(pattern = ApplicationConstants.DATE_FORMAT_WITH_TIME)
    private LocalDateTime activationDate;
}
