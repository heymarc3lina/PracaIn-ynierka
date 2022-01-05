package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    Airport getByCity(String city);
}
