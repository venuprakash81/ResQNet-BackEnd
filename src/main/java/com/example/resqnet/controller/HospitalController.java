package com.example.resqnet.controller;

import com.example.resqnet.model.Hospital;
import com.example.resqnet.repository.HospitalRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "https://res-q-net-front-end-venu-prakash.vercel.app")
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    public HospitalController(
            HospitalRepository hospitalRepository) {

        this.hospitalRepository = hospitalRepository;
    }

    // ==========================================
    // HOSPITAL REGISTRATION
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerHospital(
            @RequestBody Hospital hospital) {

        Map<String, Object> response =
                new HashMap<>();

        // Check email
        if (hospitalRepository.existsByEmail(
                hospital.getEmail())) {

            response.put(
                    "message",
                    "This email is already registered."
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        // Check hospital ID
        if (hospitalRepository.existsByHospitalId(
                hospital.getHospitalId())) {

            response.put(
                    "message",
                    "This Hospital ID is already registered."
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        // Save hospital
        Hospital savedHospital =
                hospitalRepository.save(hospital);

        response.put(
                "message",
                "Hospital registered successfully."
        );

        response.put(
                "hospitalId",
                savedHospital.getHospitalId()
        );

        response.put(
                "email",
                savedHospital.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ==========================================
    // HOSPITAL LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> loginHospital(
            @RequestBody Map<String, String> loginData) {

        Map<String, Object> response =
                new HashMap<>();

        String email =
                loginData.get("email");

        String password =
                loginData.get("password");

        // Check empty values
        if (email == null ||
                password == null ||
                email.isEmpty() ||
                password.isEmpty()) {

            response.put(
                    "message",
                    "Email and password are required."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        // Find hospital
        Optional<Hospital> hospitalOptional =
                hospitalRepository.findByEmail(email);

        if (hospitalOptional.isEmpty()) {

            response.put(
                    "message",
                    "Hospital account not found."
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        Hospital hospital =
                hospitalOptional.get();

        // Check password
        if (!hospital.getPassword().equals(password)) {

            response.put(
                    "message",
                    "Invalid password."
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // Remove password before sending to frontend
        hospital.setPassword(null);

        response.put(
                "message",
                "Hospital login successful."
        );

        response.put(
                "hospital",
                hospital
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
    @GetMapping
public ResponseEntity<?> getAllHospitals() {
    return ResponseEntity.ok(
        hospitalRepository.findAll().stream()
            .map(hospital -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", hospital.getId());
                data.put("hospitalId", hospital.getHospitalId());
                data.put("hospitalName", hospital.getHospitalName());
                data.put("email", hospital.getEmail());
                data.put("phone", hospital.getPhone());
                data.put("address", hospital.getAddress());
                data.put("city", hospital.getCity());
                return data;
            })
            .toList()
    );
}
}
