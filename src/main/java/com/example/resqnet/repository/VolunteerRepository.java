package com.example.resqnet.repository;

import com.example.resqnet.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository
        extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}