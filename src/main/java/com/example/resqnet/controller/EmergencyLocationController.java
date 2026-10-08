package com.example.resqnet.controller;

import com.example.resqnet.model.EmergencyLocation;
import com.example.resqnet.repository.EmergencyLocationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/emergency-locations")
@CrossOrigin(origins = "http://localhost:3000")
public class EmergencyLocationController {

    private final EmergencyLocationRepository repository;

    public EmergencyLocationController(
            EmergencyLocationRepository repository) {
        this.repository = repository;
    }

    // Get all emergency locations
    @GetMapping
    public List<EmergencyLocation> getAllLocations() {
        return repository.findAll();
    }

    // Get one emergency location
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocation(@PathVariable Long id) {
        Optional<EmergencyLocation> location = repository.findById(id);

        if (location.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Emergency location not found"));
        }

        return ResponseEntity.ok(location.get());
    }

    // Add a new emergency location
    @PostMapping
    public ResponseEntity<?> addLocation(
            @RequestBody EmergencyLocation location) {

        String validationError = validateLocation(location);

        if (validationError != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", validationError));
        }

        location.setId(null);
        EmergencyLocation saved = repository.save(location);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update an existing emergency location
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long id,
            @RequestBody EmergencyLocation updatedLocation) {

        Optional<EmergencyLocation> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Emergency location not found"));
        }

        String validationError = validateLocation(updatedLocation);

        if (validationError != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", validationError));
        }

        EmergencyLocation existing = optional.get();

        existing.setTitle(updatedLocation.getTitle().trim());
        existing.setEmergencyType(updatedLocation.getEmergencyType().trim());
        existing.setDescription(updatedLocation.getDescription());
        existing.setAddress(updatedLocation.getAddress().trim());
        existing.setLatitude(updatedLocation.getLatitude());
        existing.setLongitude(updatedLocation.getLongitude());
        existing.setContactNumber(updatedLocation.getContactNumber());

        return ResponseEntity.ok(repository.save(existing));
    }

    // Delete an emergency location
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Emergency location not found"));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Emergency location deleted successfully")
        );
    }

    private String validateLocation(EmergencyLocation location) {
        if (location.getTitle() == null ||
                location.getTitle().isBlank()) {
            return "Emergency title is required";
        }

        if (location.getEmergencyType() == null ||
                location.getEmergencyType().isBlank()) {
            return "Emergency type is required";
        }

        if (location.getAddress() == null ||
                location.getAddress().isBlank()) {
            return "Address is required";
        }

        if (location.getLatitude() == null ||
                location.getLongitude() == null) {
            return "Latitude and longitude are required";
        }

        if (location.getLatitude() < -90 ||
                location.getLatitude() > 90 ||
                location.getLongitude() < -180 ||
                location.getLongitude() > 180) {
            return "Invalid latitude or longitude";
        }

        return null;
    }
}