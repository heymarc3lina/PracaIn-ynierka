package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.CreateUserDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.enums.RoleType;
import com.agatapietrzycka.ticketreservation.service.TokenService;
import com.agatapietrzycka.ticketreservation.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/ticketreservation/api/register")
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createUserAccount(@RequestBody final CreateUserDto createUserDto) {
        return userService.createUser(createUserDto, Set.of(RoleType.USER));
    }

    @PostMapping("/manager")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDto createManagerAccount(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto, Set.of(RoleType.MANAGER));
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDto createAdminAccount(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto, Set.of(RoleType.ADMIN));
    }

    @GetMapping("/activate/{token}")
    @ResponseStatus(HttpStatus.OK)
    public String activateAccount(@PathVariable("token") final String token) {
        tokenService.activateAccountByToken(token);
        return "Account activated successfully!";
    }
}
