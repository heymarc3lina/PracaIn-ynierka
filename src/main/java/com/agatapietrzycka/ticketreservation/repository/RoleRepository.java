package com.agatapietrzycka.ticketreservation.repository;

import com.agatapietrzycka.ticketreservation.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
