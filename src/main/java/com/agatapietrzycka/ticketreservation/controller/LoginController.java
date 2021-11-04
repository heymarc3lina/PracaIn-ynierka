package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.config.LoginCredentials;
import com.agatapietrzycka.ticketreservation.model.Role;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.util.exception.CustomUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getRole(@AuthenticationPrincipal User user){
        Role role = user.getRoles().stream().findFirst().orElseThrow(() -> new CustomUserException("User does not have any role"));
        return ResponseEntity.ok(role.getRole().name());
    }
}
