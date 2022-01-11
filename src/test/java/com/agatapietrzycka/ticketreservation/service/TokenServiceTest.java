package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.entity.Role;
import com.agatapietrzycka.ticketreservation.entity.Token;
import com.agatapietrzycka.ticketreservation.entity.User;
import com.agatapietrzycka.ticketreservation.entity.enums.RoleType;
import com.agatapietrzycka.ticketreservation.repository.RoleRepository;
import com.agatapietrzycka.ticketreservation.repository.TokenRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.ActivationTokenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TokenServiceTest {
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private TokenService tokenService;
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
    public void createActivationTokenTest() {
        //given:
        String time = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE);
        //when:
        Token token = tokenService.createActivationToken(user);
        //then:
        assertEquals(user.getUserId(), token.getUser().getUserId());
        assertEquals(time, token.getExpiryDate().format(DateTimeFormatter.ISO_DATE));
        assertFalse(userRepository.findById(user.getUserId()).get().isActive());
    }


    @Test
    public void activateAccountByTokenTest() {
        //given:
        //when:
        tokenService.activateAccountByToken(token.getToken());
        //then:
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                userRepository.findByEmail(user.getEmail()).get().getActivationDate().format(DateTimeFormatter.ISO_DATE));
        assertTrue(userRepository.findByEmail(user.getEmail()).get().isActive());
        assertFalse(tokenRepository.findByToken(token.getToken()).isPresent());


    }

    @Test
    public void activateAccountByTokenCase1Test() {
        //given:
        String badToken = "badToken";
        //when:
        //then:
        assertThatExceptionOfType(ActivationTokenException.class)
                .isThrownBy(() -> tokenService.activateAccountByToken(badToken))
                .withMessage("Token is not connected with user!");
    }

    @Test
    public void activateAccountByTokenCase2Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(ActivationTokenException.class)
                .isThrownBy(() -> tokenService.activateAccountByToken(token2.getToken()))
                .withMessage("User account is already activated!");
    }

    @Test
    public void activateAccountByTokenCase3Test() {
        //given:
        //when:
        //then:
        assertThatExceptionOfType(ActivationTokenException.class)
                .isThrownBy(() -> tokenService.activateAccountByToken(token3.getToken()))
                .withMessage("Token has already expired!");
    }
}
