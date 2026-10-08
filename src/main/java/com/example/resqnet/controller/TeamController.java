package com.example.resqnet.controller;

import com.example.resqnet.model.Team;
import com.example.resqnet.repository.TeamRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "https://res-q-net-front-end-venu-prakash.vercel.app")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }


    // =====================================================
    // TEAM REGISTRATION
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerTeam(
            @RequestBody Team team) {

        try {

            if (team.getEmail() == null ||
                team.getEmail().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            createMessage(
                                "Email is required."
                            )
                        );
            }


            if (team.getPhone() == null ||
                team.getPhone().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            createMessage(
                                "Phone number is required."
                            )
                        );
            }


            // Check duplicate email

            if (teamRepository
                    .findByEmail(team.getEmail())
                    .isPresent()) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(
                            createMessage(
                                "A team with this email already exists."
                            )
                        );
            }


            // Save team

            Team savedTeam =
                    teamRepository.save(team);


            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Rescue team registered successfully!"
            );

            response.put(
                    "teamId",
                    savedTeam.getId()
            );

            response.put(
                    "team",
                    savedTeam
            );


            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        }

        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                        createMessage(
                            "Team registration failed."
                        )
                    );
        }
    }


    // =====================================================
    // TEAM LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> loginTeam(
            @RequestBody Map<String, String> loginData) {

        try {

            String email =
                    loginData.get("email");

            String phone =
                    loginData.get("phone");


            if (email == null ||
                email.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            createMessage(
                                "Email is required."
                            )
                        );
            }


            if (phone == null ||
                phone.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                            createMessage(
                                "Phone number is required."
                            )
                        );
            }


            // Find team by email

            var teamOptional =
                    teamRepository.findByEmail(email);


            if (teamOptional.isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                            createMessage(
                                "Team not found. Please register first."
                            )
                        );
            }


            Team team =
                    teamOptional.get();


            // Verify phone

            if (!team.getPhone().equals(phone)) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                            createMessage(
                                "Invalid email or phone number."
                            )
                        );
            }


            // Successful login

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Team login successful!"
            );

            response.put(
                    "team",
                    team
            );


            return ResponseEntity.ok(response);

        }

        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                        createMessage(
                            "Login failed."
                        )
                    );
        }
    }


    // =====================================================
    // GET ALL TEAMS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {

        return ResponseEntity.ok(
                teamRepository.findAll()
        );
    }


    // =====================================================
    // GET TEAM BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamById(
            @PathVariable Long id) {

        return teamRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                    ResponseEntity.notFound().build()
                );
    }


    // =====================================================
    // DELETE TEAM
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(
            @PathVariable Long id) {

        if (!teamRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        teamRepository.deleteById(id);


        return ResponseEntity.ok(
                createMessage(
                    "Team deleted successfully."
                )
        );
    }


    // =====================================================
    // HELPER METHOD
    // =====================================================

    private Map<String, String> createMessage(
            String message) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                message
        );

        return response;
    }
}
