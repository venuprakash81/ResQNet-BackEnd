package com.example.resqnet.repository;

import com.example.resqnet.model.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyRequestRepository
        extends JpaRepository<EmergencyRequest, Long> {

    List<EmergencyRequest> findByCitizenEmail(String citizenEmail);
}