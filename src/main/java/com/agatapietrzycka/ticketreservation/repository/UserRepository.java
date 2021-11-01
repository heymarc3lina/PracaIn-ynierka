package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Long> {
    Optional<User> findByEmail(String email);
}
