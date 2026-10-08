package com.example.resqnet.controller;

import com.example.resqnet.model.EmergencyRequest;
import com.example.resqnet.repository.EmergencyRequestRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

        @RestController
@RequestMapping("/api/emergencies")
@CrossOrigin(origins = "http://localhost:3000")
public class EmergencyController {

    private final EmergencyRequestRepository repository;

    private final String uploadDirectory =
            "uploads/emergencies/";

    public EmergencyController(
            EmergencyRequestRepository repository) {

        this.repository = repository;
    }

    // =========================================================
    // REPORT EMERGENCY
    // =========================================================

    @PostMapping(
            value = "/report",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> reportEmergency(

            @RequestParam("citizenEmail")
            String citizenEmail,

            @RequestParam("emergencyType")
            String emergencyType,

            @RequestParam("severity")
            String severity,

            @RequestParam("location")
            String location,

            @RequestParam("latitude")
            Double latitude,

            @RequestParam("longitude")
            Double longitude,

            @RequestParam("description")
            String description,

            @RequestParam("contactNumber")
            String contactNumber,

            @RequestParam(value = "image", required = false)
            MultipartFile image
    ) {

        try {

            EmergencyRequest emergency =
                    new EmergencyRequest();

            emergency.setCitizenEmail(citizenEmail);
            emergency.setEmergencyType(emergencyType);
            emergency.setSeverity(severity);
            emergency.setLocation(location);
            emergency.setLatitude(latitude);
            emergency.setLongitude(longitude);
            emergency.setDescription(description);
            emergency.setContactNumber(contactNumber);

            // =================================================
            // IMAGE
            // =================================================

            if (image != null && !image.isEmpty()) {

                Path uploadPath =
                        Paths.get(uploadDirectory);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName =
                        image.getOriginalFilename();

                String fileName =
                        System.currentTimeMillis()
                                + "_"
                                + originalFileName;

                Path filePath =
                        uploadPath.resolve(fileName);

                Files.copy(
                        image.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                emergency.setImagePath(
                        "/uploads/emergencies/"
                                + fileName
                );

            } else {

                emergency.setImagePath(
                        "/images/default-emergency.jpg"
                );
            }

            emergency.setStatus("PENDING");

            EmergencyRequest saved =
                    repository.save(emergency);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Image upload failed: "
                                    + e.getMessage()
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Emergency report failed: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // GET ONLY PARTICULAR CITIZEN'S EMERGENCIES
    // =========================================================

    @GetMapping("/citizen/{email}")
    public ResponseEntity<List<EmergencyRequest>>
    getCitizenEmergencies(
            @PathVariable String email) {

        return ResponseEntity.ok(
                repository.findByCitizenEmail(email)
        );
    }


    // =========================================================
    // GET SINGLE EMERGENCY
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmergency(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================================
    // EDIT EMERGENCY
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmergency(

            @PathVariable Long id,

            @RequestParam("citizenEmail")
            String citizenEmail,

            @RequestParam("emergencyType")
            String emergencyType,

            @RequestParam("severity")
            String severity,

            @RequestParam("location")
            String location,

            @RequestParam("latitude")
            Double latitude,

            @RequestParam("longitude")
            Double longitude,

            @RequestParam("description")
            String description,

            @RequestParam("contactNumber")
            String contactNumber
    ) {

        return repository.findById(id)
                .map(emergency -> {

                    // IMPORTANT:
                    // Only the owner can edit
                    if (!emergency.getCitizenEmail()
                            .equalsIgnoreCase(citizenEmail)) {

                        return ResponseEntity
                                .status(403)
                                .body(
                                        "You are not allowed to edit this emergency."
                                );
                    }

                    emergency.setEmergencyType(
                            emergencyType
                    );

                    emergency.setSeverity(
                            severity
                    );

                    emergency.setLocation(
                            location
                    );

                    emergency.setLatitude(
                            latitude
                    );

                    emergency.setLongitude(
                            longitude
                    );

                    emergency.setDescription(
                            description
                    );

                    emergency.setContactNumber(
                            contactNumber
                    );

                    return ResponseEntity.ok(
                            repository.save(emergency)
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================================
    // DELETE EMERGENCY
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmergency(

            @PathVariable Long id,

            @RequestParam("citizenEmail")
            String citizenEmail
    ) {

        return repository.findById(id)
                .map(emergency -> {

                    // IMPORTANT:
                    // Only owner can delete
                    if (!emergency.getCitizenEmail()
                            .equalsIgnoreCase(citizenEmail)) {

                        return ResponseEntity
                                .status(403)
                                .body(
                                        "You are not allowed to delete this emergency."
                                );
                    }

                    repository.delete(emergency);

                    return ResponseEntity.ok(
                            "Emergency deleted successfully."
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================================
    // GET ALL EMERGENCIES
    // Used later by Rescue Team
    // =========================================================

    @GetMapping
    public ResponseEntity<List<EmergencyRequest>>
    getAllEmergencies() {

        return ResponseEntity.ok(
                repository.findAll()
        );
    }


    // =========================================================
    // UPDATE STATUS
    // Used by Rescue Team
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(

            @PathVariable Long id,

            @RequestParam String status
    ) {

        return repository.findById(id)
                .map(emergency -> {

                    emergency.setStatus(status);

                    return ResponseEntity.ok(
                            repository.save(emergency)
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }
}