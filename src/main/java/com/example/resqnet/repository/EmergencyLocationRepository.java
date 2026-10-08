package com.example.resqnet.repository;

import com.example.resqnet.model.EmergencyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyLocationRepository
        extends JpaRepository<EmergencyLocation, Long> {
}