package com.example.resqnet.controller;

import com.example.resqnet.model.Citizen;
import com.example.resqnet.repository.CitizenRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizens")
@CrossOrigin(origins = "https://res-q-net-front-end-venu-prakash.vercel.app")
public class CitizenController {

    private final CitizenRepository citizenRepository;

    public CitizenController(CitizenRepository citizenRepository) {
        this.citizenRepository = citizenRepository;
    }

    // =========================================================
    // CITIZEN REGISTRATION
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerCitizen(
            @RequestBody Citizen citizen) {

        Map<String, Object> response = new HashMap<>();

        // Check email
        if (citizenRepository.existsByEmail(citizen.getEmail())) {

            response.put(
                    "message",
                    "An account already exists with this email."
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        // Check phone
        if (citizenRepository.existsByPhone(citizen.getPhone())) {

            response.put(
                    "message",
                    "An account already exists with this phone number."
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        // Save citizen
        Citizen savedCitizen =
                citizenRepository.save(citizen);

        response.put(
                "message",
                "Citizen registration successful!"
        );

        response.put(
                "citizenId",
                savedCitizen.getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // CITIZEN LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> loginCitizen(
            @RequestBody Map<String, String> loginRequest) {

        Map<String, Object> response = new HashMap<>();

        // Get login details
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // =====================================================
        // VALIDATE INPUT
        // =====================================================

        if (email == null ||
                email.isBlank() ||
                password == null ||
                password.isBlank()) {

            response.put(
                    "message",
                    "Email and password are required."
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        // =====================================================
        // FIND CITIZEN BY EMAIL
        // =====================================================

        Optional<Citizen> citizenOptional =
                citizenRepository.findByEmail(email);

        if (citizenOptional.isEmpty()) {

            response.put(
                    "message",
                    "No citizen account found with this email."
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        Citizen citizen = citizenOptional.get();

        // =====================================================
        // CHECK PASSWORD
        // =====================================================

        if (!citizen.getPassword().equals(password)) {

            response.put(
                    "message",
                    "Incorrect password."
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // =====================================================
        // LOGIN SUCCESS
        // =====================================================

        Map<String, Object> citizenData = new HashMap<>();

        citizenData.put("id", citizen.getId());
        citizenData.put("fullName", citizen.getFullName());
        citizenData.put("email", citizen.getEmail());
        citizenData.put("phone", citizen.getPhone());
        citizenData.put("dateOfBirth", citizen.getDateOfBirth());
        citizenData.put("gender", citizen.getGender());
        citizenData.put("address", citizen.getAddress());
        citizenData.put("city", citizen.getCity());
        citizenData.put("state", citizen.getState());
        citizenData.put("pincode", citizen.getPincode());
        citizenData.put(
                "emergencyContactName",
                citizen.getEmergencyContactName()
        );
        citizenData.put(
                "emergencyContactPhone",
                citizen.getEmergencyContactPhone()
        );

        response.put(
                "message",
                "Citizen login successful!"
        );

        response.put(
                "citizen",
                citizenData
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping
public ResponseEntity<?> getAllCitizens() {
    return ResponseEntity.ok(
        citizenRepository.findAll().stream()
            .map(citizen -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", citizen.getId());
                data.put("fullName", citizen.getFullName());
                data.put("email", citizen.getEmail());
                data.put("phone", citizen.getPhone());
                data.put("city", citizen.getCity());
                data.put("state", citizen.getState());
                return data;
            })
            .toList()
    );
}
@GetMapping("/{id}")
public ResponseEntity<?> getCitizenById(@PathVariable Long id) {

    Optional<Citizen> optional = citizenRepository.findById(id);

    if (optional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Citizen not found"));
    }

    Citizen citizen = optional.get();

    Map<String, Object> data = new HashMap<>();
    data.put("id", citizen.getId());
    data.put("fullName", citizen.getFullName());
    data.put("email", citizen.getEmail());
    data.put("phone", citizen.getPhone());
    data.put("dateOfBirth", citizen.getDateOfBirth());
    data.put("gender", citizen.getGender());
    data.put("address", citizen.getAddress());
    data.put("city", citizen.getCity());
    data.put("state", citizen.getState());
    data.put("pincode", citizen.getPincode());
    data.put("emergencyContactName", citizen.getEmergencyContactName());
    data.put("emergencyContactPhone", citizen.getEmergencyContactPhone());

    return ResponseEntity.ok(data);
}
// GET CITIZEN PROFILE BY EMAIL
@GetMapping("/email/{email}")
public ResponseEntity<?> getCitizenByEmail(
        @PathVariable String email) {

    Optional<Citizen> optional =
            citizenRepository.findByEmail(email);

    if (optional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Citizen not found"));
    }

    Citizen citizen = optional.get();

    Map<String, Object> data = new HashMap<>();
    data.put("id", citizen.getId());
    data.put("fullName", citizen.getFullName());
    data.put("email", citizen.getEmail());
    data.put("phone", citizen.getPhone());
    data.put("dateOfBirth", citizen.getDateOfBirth());
    data.put("gender", citizen.getGender());
    data.put("address", citizen.getAddress());
    data.put("city", citizen.getCity());
    data.put("state", citizen.getState());
    data.put("pincode", citizen.getPincode());
    data.put("emergencyContactName",
            citizen.getEmergencyContactName());
    data.put("emergencyContactPhone",
            citizen.getEmergencyContactPhone());

    return ResponseEntity.ok(data);
}


// UPDATE CITIZEN PROFILE BY EMAIL
@PutMapping("/email/{email}")
public ResponseEntity<?> updateCitizenByEmail(
        @PathVariable String email,
        @RequestBody Citizen updatedCitizen) {

    Optional<Citizen> optional =
            citizenRepository.findByEmail(email);

    if (optional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Citizen not found"));
    }

    Citizen citizen = optional.get();

    if (updatedCitizen.getFullName() == null ||
            updatedCitizen.getFullName().isBlank() ||
            updatedCitizen.getEmail() == null ||
            updatedCitizen.getEmail().isBlank() ||
            updatedCitizen.getPhone() == null ||
            updatedCitizen.getPhone().isBlank()) {

        return ResponseEntity.badRequest()
                .body(Map.of("message",
                        "Name, email, and phone are required"));
    }

    String newEmail = updatedCitizen.getEmail().trim();
    String newPhone = updatedCitizen.getPhone().trim();

    if (citizenRepository.existsByEmailAndIdNot(
            newEmail, citizen.getId())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message",
                        "Email is already registered"));
    }

    if (citizenRepository.existsByPhoneAndIdNot(
            newPhone, citizen.getId())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message",
                        "Phone number is already registered"));
    }

    citizen.setFullName(updatedCitizen.getFullName().trim());
    citizen.setEmail(newEmail);
    citizen.setPhone(newPhone);
    citizen.setDateOfBirth(updatedCitizen.getDateOfBirth());
    citizen.setGender(updatedCitizen.getGender());
    citizen.setAddress(updatedCitizen.getAddress());
    citizen.setCity(updatedCitizen.getCity());
    citizen.setState(updatedCitizen.getState());
    citizen.setPincode(updatedCitizen.getPincode());
    citizen.setEmergencyContactName(
            updatedCitizen.getEmergencyContactName());
    citizen.setEmergencyContactPhone(
            updatedCitizen.getEmergencyContactPhone());

    Citizen saved = citizenRepository.save(citizen);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Profile updated successfully!");
    response.put("id", saved.getId());
    response.put("fullName", saved.getFullName());
    response.put("email", saved.getEmail());
    response.put("phone", saved.getPhone());

    return ResponseEntity.ok(response);
}
@PutMapping("/email/{email}/change-password")
public ResponseEntity<Map<String, String>> changePassword(
        @PathVariable String email,
        @RequestBody ChangePasswordRequest request) {

    Optional<Citizen> optionalCitizen = citizenRepository.findByEmail(email);

    if (optionalCitizen.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Citizen account not found."));
    }

    Citizen citizen = optionalCitizen.get();

    if (request.getCurrentPassword() == null ||
            !request.getCurrentPassword().equals(citizen.getPassword())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Current password is incorrect."));
    }

    if (request.getNewPassword() == null ||
            request.getNewPassword().length() < 8) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "New password must contain at least 8 characters."));
    }

    citizen.setPassword(request.getNewPassword());
    citizenRepository.save(citizen);

    return ResponseEntity.ok(
            Map.of("message", "Password changed successfully.")
    );
}

public static class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

// UPDATE PROFILE
@PutMapping("/{id}")
public ResponseEntity<?> updateCitizen(
        @PathVariable Long id,
        @RequestBody Citizen updatedCitizen) {

    Optional<Citizen> optional = citizenRepository.findById(id);

    if (optional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Citizen not found"));
    }

    Citizen citizen = optional.get();

    if (updatedCitizen.getFullName() == null ||
            updatedCitizen.getFullName().isBlank() ||
            updatedCitizen.getEmail() == null ||
            updatedCitizen.getEmail().isBlank() ||
            updatedCitizen.getPhone() == null ||
            updatedCitizen.getPhone().isBlank()) {

        return ResponseEntity.badRequest()
                .body(Map.of("message",
                        "Name, email, and phone are required"));
    }

    if (citizenRepository.existsByEmailAndIdNot(
            updatedCitizen.getEmail(), id)) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message",
                        "Email is already registered"));
    }

    if (citizenRepository.existsByPhoneAndIdNot(
            updatedCitizen.getPhone(), id)) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message",
                        "Phone number is already registered"));
    }

    // Update only editable fields
    citizen.setFullName(updatedCitizen.getFullName().trim());
    citizen.setEmail(updatedCitizen.getEmail().trim());
    citizen.setPhone(updatedCitizen.getPhone().trim());
    citizen.setDateOfBirth(updatedCitizen.getDateOfBirth());
    citizen.setGender(updatedCitizen.getGender());
    citizen.setAddress(updatedCitizen.getAddress());
    citizen.setCity(updatedCitizen.getCity());
    citizen.setState(updatedCitizen.getState());
    citizen.setPincode(updatedCitizen.getPincode());
    citizen.setEmergencyContactName(
            updatedCitizen.getEmergencyContactName());
    citizen.setEmergencyContactPhone(
            updatedCitizen.getEmergencyContactPhone());

    Citizen saved = citizenRepository.save(citizen);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Profile updated successfully!");
    response.put("id", saved.getId());
    response.put("fullName", saved.getFullName());
    response.put("email", saved.getEmail());
    response.put("phone", saved.getPhone());

    return ResponseEntity.ok(response);
}
}
