package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

    Optional<User> findByEmail(String email);
}
