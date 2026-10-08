package com.example.resqnet.repository;

import com.example.resqnet.model.Hospital;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByEmail(String email);

    Optional<Hospital> findByHospitalId(String hospitalId);

    boolean existsByEmail(String email);

    boolean existsByHospitalId(String hospitalId);
}