package com.example.resqnet.repository;

import com.example.resqnet.model.Team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository
        extends JpaRepository<Team, Long> {

    Optional<Team> findByEmail(String email);
}