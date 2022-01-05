package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.config.LoginCredentials;
import com.agatapietrzycka.ticketreservation.dto.RoleDto;
import com.agatapietrzycka.ticketreservation.entity.Role;
import com.agatapietrzycka.ticketreservation.entity.User;
import com.agatapietrzycka.ticketreservation.util.exception.CustomUserException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class LoginController {

    // just here to be able to login in swagger
    @PostMapping("/ticketreservation/api/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody final LoginCredentials loginCredentials) {
    }

    @GetMapping("/ticketreservation/api/whoami")
    @ResponseStatus(HttpStatus.OK)
    public RoleDto getRole(@AuthenticationPrincipal User user) {
        Role role = user.getRoles().stream().findFirst().orElseThrow(() -> new CustomUserException("User does not have any role"));
        return new RoleDto(role.getRole().name());
    }
}
