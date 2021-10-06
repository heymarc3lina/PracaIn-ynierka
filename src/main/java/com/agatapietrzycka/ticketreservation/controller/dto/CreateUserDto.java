package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;

@Getter
public class CreateUserDto {
    String name;
    String surname;
    String email;
    String password;

}
