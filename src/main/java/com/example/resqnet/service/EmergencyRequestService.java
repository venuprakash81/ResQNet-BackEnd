package com.example.resqnet.service;

import com.example.resqnet.model.EmergencyRequest;
import com.example.resqnet.repository.EmergencyRequestRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository repository;

    public EmergencyRequestService(
            EmergencyRequestRepository repository) {
        this.repository = repository;
    }

    public EmergencyRequest saveEmergency(
            EmergencyRequest emergencyRequest) {

        return repository.save(emergencyRequest);
    }

    public List<EmergencyRequest> getAllEmergencies() {

        return repository.findAll();
    }

    public EmergencyRequest getEmergencyById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Emergency request not found"
                        ));
    }

    public EmergencyRequest updateStatus(
            Long id,
            String status) {

        EmergencyRequest emergency =
                getEmergencyById(id);

        emergency.setStatus(status);

        return repository.save(emergency);
    }
}