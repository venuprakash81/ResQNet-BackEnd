package com.example.resqnet.repository;

import com.example.resqnet.model.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbulanceRepository
        extends JpaRepository<Ambulance, Long> {

    List<Ambulance> findByHospitalId(Long hospitalId);
}