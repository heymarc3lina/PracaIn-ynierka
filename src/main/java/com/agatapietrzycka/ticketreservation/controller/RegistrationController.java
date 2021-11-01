package com.agatapietrzycka.ticketreservation.controller;

import com.agatapietrzycka.ticketreservation.controller.dto.CreateUserDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.enums.RoleType;
import com.agatapietrzycka.ticketreservation.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/ticketreservation/api/register")
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<ResponseDto> createUserAccount(@RequestBody CreateUserDto createUserDto){
        ResponseDto responseDto = userService.createUser(createUserDto, Set.of(RoleType.USER));
        return getResponseWithCorrectStatusCode(responseDto);
    }

    @PostMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDto> createManagerAccount(@RequestBody CreateUserDto createUserDto){
        ResponseDto responseDto = userService.createUser(createUserDto, Set.of(RoleType.MANAGER));
        return getResponseWithCorrectStatusCode(responseDto);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDto> createAdminAccount(@RequestBody CreateUserDto createUserDto){
        ResponseDto responseDto = userService.createUser(createUserDto, Set.of(RoleType.ADMIN));
        return getResponseWithCorrectStatusCode(responseDto);
    }


    private ResponseEntity<ResponseDto> getResponseWithCorrectStatusCode(ResponseDto responseDto) {
        HttpStatus status = HttpStatus.CREATED;
        if(!responseDto.getErrorMessage().isEmpty()){
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(responseDto);
    }
}
