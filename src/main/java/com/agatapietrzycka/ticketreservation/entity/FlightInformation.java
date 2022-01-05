package com.agatapietrzycka.ticketreservation.entity;

import com.agatapietrzycka.ticketreservation.entity.enums.FlightStatus;
import com.agatapietrzycka.ticketreservation.util.FlightStatusEnumConverter;
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
public class FlightInformation {

    @Id
    @SequenceGenerator(
            name = "flight_info_seq",
            sequenceName = "flight_info_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight_info_seq")
    private Long id;

    @Convert(converter = FlightStatusEnumConverter.class)
    private FlightStatus status;

    private Instant updatedAt;

    @OneToOne(mappedBy = "flightInformation")
    private Flight flight;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

}
