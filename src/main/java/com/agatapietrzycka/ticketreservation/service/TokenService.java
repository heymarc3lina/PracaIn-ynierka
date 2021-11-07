package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.model.Token;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.repository.TokenRepository;
import com.agatapietrzycka.ticketreservation.util.exception.ActivationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final TemporalUnit TOKEN_ALIVE_UNIT = ChronoUnit.DAYS;
    private static final long TOKEN_ALIVE_VALUE = 7L;
    private final TokenRepository tokenRepository;

    @Transactional
    public void activateAccountByToken(final String tokenValue) {
        final Token token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ActivationTokenException("Token is not connected with user!"));
        if (token.hasExpired()) {
            throw new ActivationTokenException("Token has already expired!");
        }
        final User user = token.getUser();
        if (user.isActive()) {
            throw new ActivationTokenException("User account is already activated!");
        }
        activateUserAccount(user);
        tokenRepository.delete(token);
    }

    private void activateUserAccount(final User user) {
        user.setActive(true);
        user.setActivationDate(LocalDateTime.now());
    }

    @Transactional
    public Token createActivationToken(final User user) {
        final Token token = new Token(user, TOKEN_ALIVE_UNIT, TOKEN_ALIVE_VALUE);
        return tokenRepository.save(token);
    }
}
