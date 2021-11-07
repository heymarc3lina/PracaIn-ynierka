package com.agatapietrzycka.ticketreservation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    @SequenceGenerator(
            name = "token_seq",
            sequenceName = "token_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    private Long tokenId;
    private String token;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime expiryDate;

    public Token(final User user, final TemporalUnit expirationUnit, final long expirationValue) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        expiryDate = LocalDateTime.now().plus(expirationValue, expirationUnit);
    }

    public boolean hasExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

}

