package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.dto.CreateUserDto;
import com.agatapietrzycka.ticketreservation.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.dto.UserDto;
import com.agatapietrzycka.ticketreservation.entity.Role;
import com.agatapietrzycka.ticketreservation.entity.Token;
import com.agatapietrzycka.ticketreservation.entity.User;
import com.agatapietrzycka.ticketreservation.entity.enums.RoleType;
import com.agatapietrzycka.ticketreservation.repository.RoleRepository;
import com.agatapietrzycka.ticketreservation.repository.TokenRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomUserException;
import com.agatapietrzycka.ticketreservation.validation.ApplicationConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User user, user1;
    private Token token, token2, token3;


    @BeforeEach
    public void setUp() {

        Role newRole = new Role();
        newRole.setRole(RoleType.USER);
        Role role = roleRepository.save(newRole);

        User newUser = new User();
        newUser.setCreatedDate(Instant.now());
        newUser.setActive(false);
        newUser.setEmail("user@user.pl");
        newUser.setPassword("12345");
        newUser.setSurname("User");
        newUser.setName("Testowy");
        newUser.setRoles(Set.of(role));
        user = userRepository.save(newUser);

        Token newToken = new Token(user, ChronoUnit.DAYS, 7L);
        newToken.setUser(user);
        token = tokenRepository.save(newToken);

        User newUser1 = new User();
        newUser1.setCreatedDate(Instant.now());
        newUser1.setActivationDate(LocalDateTime.now());
        newUser1.setActive(true);
        newUser1.setEmail("user1@user.pl");
        newUser1.setPassword("12345");
        newUser1.setSurname("User");
        newUser1.setName("Testowy");
        newUser1.setRoles(Set.of(role));
        user1 = userRepository.save(newUser1);

        Token newToken1 = new Token(user, ChronoUnit.DAYS, 7L);
        newToken1.setUser(user1);
        token2 = tokenRepository.save(newToken1);

        Token newToken2 = new Token(user, ChronoUnit.MILLIS, 1L);
        newToken2.setUser(user1);
        token3 = tokenRepository.save(newToken2);
    }

    @AfterEach
    public void clean() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void createUserTest() {
        //given:
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("user12@user.pl");
        dto.setPassword("12345");
        dto.setName("User");
        dto.setSurname("User");

        //when:
        ResponseDto responseDto = userService.createUser(dto, Set.of(RoleType.USER));
        //then:
        assertEquals(0, responseDto.getErrorMessage().size());
        assertNotNull(responseDto.getId());
    }

    @Test
    public void createUserCase1Test() {
        //given:
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(user.getEmail());
        dto.setPassword("12345");
        dto.setName("User");
        dto.setSurname("User");

        //when:
        //then:
        assertThatExceptionOfType(CustomUserException.class)
                .isThrownBy(() -> userService.createUser(dto, Set.of(RoleType.USER)))
                .withMessage("Email: user@user.pl is already in use!");
    }

    @Test
    public void getUsers() {
        //given:
        //when:
        List<UserDto> userDtos = userService.getUsers();
        //then:
        assertEquals(2, userDtos.size());
        assertEquals(user.getEmail(), userDtos.get(0).getEmail());
        assertEquals(user.getSurname(), userDtos.get(0).getSurname());
        assertEquals(user.getName(), userDtos.get(0).getName());
        assertEquals(RoleType.USER.name(), userDtos.get(0).getRole());
        assertEquals(user.getActivationDate(), userDtos.get(0).getActivationDate());
        assertEquals(LocalDateTime.ofInstant(user.getCreatedDate(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                userDtos.get(0).getCreatedDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(user.getUserId(), userDtos.get(0).getId());
        assertEquals(user.isActive(), userDtos.get(0).getIsActive());

        assertEquals(user1.getEmail(), userDtos.get(1).getEmail());
        assertEquals(user1.getSurname(), userDtos.get(1).getSurname());
        assertEquals(user1.getName(), userDtos.get(1).getName());
        assertEquals(RoleType.USER.name(), userDtos.get(1).getRole());
        assertEquals(user1.getActivationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME))
                , userDtos.get(1).getActivationDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(LocalDateTime.ofInstant(user1.getCreatedDate(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)),
                userDtos.get(1).getCreatedDate().format(DateTimeFormatter.ofPattern(ApplicationConstants.DATE_FORMAT_WITH_TIME)));
        assertEquals(user1.getUserId(), userDtos.get(1).getId());
        assertEquals(user1.isActive(), userDtos.get(1).getIsActive());
    }

    @Test
    public void loadUserByUsernameTest() {
        //given:
        //when:
        //then:
        UserDetails userDetails = userService.loadUserByUsername(user1.getEmail());
        assertEquals(user1.getEmail(), userDetails.getUsername());

    }

    @Test
    public void loadUserByUsernameCase1Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(user.getEmail()))
                .withMessage("Email: user@user.pl not found");
    }

    @Test
    public void loadUserByUsernameCase2Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername("userBad@user.pl"))
                .withMessage("Email: userBad@user.pl not found");
    }
}
