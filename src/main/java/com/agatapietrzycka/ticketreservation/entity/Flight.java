package com.agatapietrzycka.ticketreservation.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Flight {
    @Id
    @SequenceGenerator(
            name = "flight_seq",
            sequenceName = "flight_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight_seq")
    private Long id;
//    @NotNull
//    private Boolean isAvailable;
    @NotNull
    private LocalDateTime arrivalDate;
    @NotNull
    private LocalDateTime departureDate;
    @ManyToOne
    @JoinColumn(name = "plane_id", nullable = false)
    private Plane Plane;
    @ManyToOne
    @JoinColumn(name = "arrivalAirport_id", nullable = false)
    private Airport arrivalAirport;
    @ManyToOne
    @JoinColumn(name = "departureAirport_id", nullable = false)
    private Airport departureAirport;
    @NotNull
    private Integer price;
    @OneToOne
    @JoinColumn(name = "status_id")
    private FlightInformation flightInformation;
}
