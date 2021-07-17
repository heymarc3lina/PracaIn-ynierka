package com.agatapietrzycka.ticketreservation.model;

import com.agatapietrzycka.ticketreservation.model.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.model.enums.Status;
import com.agatapietrzycka.ticketreservation.util.FlightStatusEnumConverter;
import com.agatapietrzycka.ticketreservation.util.StatusEnumConverter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.time.Instant;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInformation {

    @Id
    @SequenceGenerator(
            name = "reservation_info_seq",
            sequenceName = "reservation_info_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_info_seq")
    private Long id;
    @Convert(converter = StatusEnumConverter.class)
    private Status status;
    private Instant updatedAt;
    @OneToOne(mappedBy = "reservationInformation")
    private Reservation reservation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

}
