package com.agatapietrzycka.ticketreservation.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateUserDto {
    @NotBlank(message = "Name cannot be empty!")
    private String name;
    @NotBlank(message = "Surname cannot be empty!")
    private String surname;
    @NotBlank(message = "Email cannot be empty!")
    private String email;
    @NotBlank(message = "Password cannot be empty!")
    @Size(min = 5, message = "Password must contain at least 5 characters!")
    private String password;

}
