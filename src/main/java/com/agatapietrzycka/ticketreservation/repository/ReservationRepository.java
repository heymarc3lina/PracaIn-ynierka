package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r.seat.seatId FROM Reservation r JOIN r.reservationInformation ri " +
            "WHERE ri.reservationStatus = 1 AND r.flight.id = :flightId")
    List<Long> findAllSeatForFligth(@Param("flightId") Long flightId);

    @Query("SELECT r FROM Reservation r  WHERE r.user.userId = :userId ")
    List<Reservation> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.flight.id = :flightId ")
    List<Reservation> findAllByFlightId(@Param("flightId") Long flightId);
}
