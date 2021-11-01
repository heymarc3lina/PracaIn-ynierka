package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;

@Getter
public class CreateUserDto {
    private String name;
    private String surname;
    private String email;
    private String password;

}
