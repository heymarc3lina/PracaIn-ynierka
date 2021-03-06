package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.entity.ReservationInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReservationInformationRepository extends JpaRepository<ReservationInformation, Long> {
}
