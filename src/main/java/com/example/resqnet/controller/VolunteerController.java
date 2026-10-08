package com.example.resqnet.controller;

import com.example.resqnet.model.Volunteer;
import com.example.resqnet.service.VolunteerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin(origins = "https://res-q-net-front-end-venu-prakash.vercel.app")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    // =========================================================
    // VOLUNTEER REGISTRATION
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerVolunteer(
            @RequestBody Volunteer volunteer) {

        try {

            Volunteer savedVolunteer =
                    volunteerService.registerVolunteer(volunteer);

            Map<String, Object> response = new HashMap<>();

            response.put(
                    "message",
                    "Volunteer registered successfully"
            );

            response.put(
                    "id",
                    savedVolunteer.getId()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            Map<String, String> response = new HashMap<>();

            response.put(
                    "message",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    // =========================================================
    // GET ALL VOLUNTEERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {

        return ResponseEntity.ok(
                volunteerService.getAllVolunteers()
        );
    }

    // =========================================================
    // VOLUNTEER LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> loginVolunteer(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        // Validate input
        if (email == null ||
                email.isBlank() ||
                password == null ||
                password.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Email and password are required"
                    ));
        }

        try {

            Volunteer volunteer =
                    volunteerService.loginVolunteer(
                            email,
                            password
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "id",
                            volunteer.getId(),

                            "fullName",
                            volunteer.getFullName(),

                            "email",
                            volunteer.getEmail(),

                            "message",
                            "Volunteer login successful"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================================================
    // GET VOLUNTEER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                volunteerService.getVolunteerById(id)
        );
    }

    

}
