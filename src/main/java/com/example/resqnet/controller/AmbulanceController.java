package com.example.resqnet.controller;

import com.example.resqnet.model.Ambulance;
import com.example.resqnet.repository.AmbulanceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "http://localhost:3000")
public class AmbulanceController {

    private final AmbulanceRepository ambulanceRepository;

    public AmbulanceController(
            AmbulanceRepository ambulanceRepository) {

        this.ambulanceRepository =
                ambulanceRepository;
    }

    // =====================================================
    // GET AMBULANCES
    // =====================================================

    @GetMapping("/{hospitalId}/ambulances")
    public ResponseEntity<List<Ambulance>> getAmbulances(
            @PathVariable Long hospitalId) {

        return ResponseEntity.ok(
                ambulanceRepository
                        .findByHospitalId(hospitalId)
        );
    }

    // =====================================================
    // ADD AMBULANCE
    // =====================================================

    @PostMapping("/{hospitalId}/ambulances")
    public ResponseEntity<?> addAmbulance(
            @PathVariable Long hospitalId,
            @RequestBody Ambulance ambulance) {

        if (ambulance.getTotalAmbulances() == null ||
                ambulance.getTotalAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid total ambulances");
        }

        if (ambulance.getAvailableAmbulances() == null ||
                ambulance.getAvailableAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid available ambulances");
        }

        if (ambulance.getAssignedAmbulances() == null ||
                ambulance.getAssignedAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid assigned ambulances");
        }

        if (ambulance.getAvailableAmbulances()
                + ambulance.getAssignedAmbulances()
                > ambulance.getTotalAmbulances()) {

            return ResponseEntity.badRequest()
                    .body(
                        "Available + assigned ambulances " +
                        "cannot exceed total ambulances"
                    );
        }

        ambulance.setId(null);

        ambulance.setHospitalId(hospitalId);

        if (ambulance.getStatus() == null ||
                ambulance.getStatus().trim().isEmpty()) {

            ambulance.setStatus("Active");
        }

        return ResponseEntity.ok(
                ambulanceRepository.save(ambulance)
        );
    }

    // =====================================================
    // UPDATE AMBULANCE
    // =====================================================

    @PutMapping("/{hospitalId}/ambulances/{ambulanceId}")
    public ResponseEntity<?> updateAmbulance(
            @PathVariable Long hospitalId,
            @PathVariable Long ambulanceId,
            @RequestBody Ambulance updated) {

        Optional<Ambulance> optional =
                ambulanceRepository.findById(
                        ambulanceId
                );

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ambulance ambulance =
                optional.get();

        if (!ambulance.getHospitalId()
                .equals(hospitalId)) {

            return ResponseEntity.badRequest()
                    .body(
                        "Ambulance does not belong " +
                        "to this hospital"
                    );
        }

        if (updated.getTotalAmbulances() == null ||
                updated.getTotalAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid total ambulances");
        }

        if (updated.getAvailableAmbulances() == null ||
                updated.getAvailableAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid available ambulances");
        }

        if (updated.getAssignedAmbulances() == null ||
                updated.getAssignedAmbulances() < 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid assigned ambulances");
        }

        if (updated.getAvailableAmbulances()
                + updated.getAssignedAmbulances()
                > updated.getTotalAmbulances()) {

            return ResponseEntity.badRequest()
                    .body(
                        "Available + assigned ambulances " +
                        "cannot exceed total ambulances"
                    );
        }

        ambulance.setAmbulanceNumber(
                updated.getAmbulanceNumber()
        );

        ambulance.setAmbulanceType(
                updated.getAmbulanceType()
        );

        ambulance.setDriverName(
                updated.getDriverName()
        );

        ambulance.setDriverPhone(
                updated.getDriverPhone()
        );

        ambulance.setTotalAmbulances(
                updated.getTotalAmbulances()
        );

        ambulance.setAvailableAmbulances(
                updated.getAvailableAmbulances()
        );

        ambulance.setAssignedAmbulances(
                updated.getAssignedAmbulances()
        );

        ambulance.setStatus(
                updated.getStatus()
        );

        return ResponseEntity.ok(
                ambulanceRepository.save(ambulance)
        );
    }

    // =====================================================
    // DELETE AMBULANCE
    // =====================================================

    @DeleteMapping("/{hospitalId}/ambulances/{ambulanceId}")
    public ResponseEntity<?> deleteAmbulance(
            @PathVariable Long hospitalId,
            @PathVariable Long ambulanceId) {

        Optional<Ambulance> optional =
                ambulanceRepository.findById(
                        ambulanceId
                );

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ambulance ambulance =
                optional.get();

        if (!ambulance.getHospitalId()
                .equals(hospitalId)) {

            return ResponseEntity.badRequest()
                    .body(
                        "Ambulance does not belong " +
                        "to this hospital"
                    );
        }

        ambulanceRepository.delete(
                ambulance
        );

        return ResponseEntity.ok(
                "Ambulance deleted successfully"
        );
    }
    @GetMapping("/ambulances/all")
public ResponseEntity<List<Ambulance>> getAllAmbulances() {
    return ResponseEntity.ok(ambulanceRepository.findAll());
}
}