package com.example.resqnet.controller;

import com.example.resqnet.model.TeamMember;
import com.example.resqnet.repository.TeamMemberRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@CrossOrigin(origins = "http://localhost:3000")
public class TeamMemberController {

    private final TeamMemberRepository teamMemberRepository;


    public TeamMemberController(
            TeamMemberRepository teamMemberRepository
    ) {
        this.teamMemberRepository = teamMemberRepository;
    }


    // =====================================================
    // ADD TEAM MEMBER
    // =====================================================

    @PostMapping
    public ResponseEntity<?> addMember(
            @RequestBody TeamMember member
    ) {

        try {

            if (member.getTeamId() == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Team ID is required"
                        );
            }


            if (
                    member.getName() == null ||
                    member.getName().trim().isEmpty()
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Member name is required"
                        );
            }


            TeamMember savedMember =
                    teamMemberRepository.save(member);


            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedMember);

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Unable to add team member: "
                                    + e.getMessage()
                    );
        }
    }


    // =====================================================
    // GET MEMBERS OF PARTICULAR TEAM
    // =====================================================

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMember>> getTeamMembers(
            @PathVariable Long teamId
    ) {

        List<TeamMember> members =
                teamMemberRepository
                        .findByTeamId(teamId);

        return ResponseEntity.ok(members);
    }


    // =====================================================
    // GET SINGLE MEMBER
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getMember(
            @PathVariable Long id
    ) {

        return teamMemberRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =====================================================
    // UPDATE MEMBER
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable Long id,
            @RequestBody TeamMember updatedMember
    ) {

        return teamMemberRepository
                .findById(id)
                .map(existingMember -> {

                    existingMember.setName(
                            updatedMember.getName()
                    );

                    existingMember.setRole(
                            updatedMember.getRole()
                    );

                    existingMember.setPhone(
                            updatedMember.getPhone()
                    );

                    existingMember.setEmail(
                            updatedMember.getEmail()
                    );

                    existingMember.setAge(
                            updatedMember.getAge()
                    );

                    existingMember.setGender(
                            updatedMember.getGender()
                    );

                    existingMember.setAvailability(
                            updatedMember.getAvailability()
                    );

                    existingMember.setSkills(
                            updatedMember.getSkills()
                    );


                    TeamMember saved =
                            teamMemberRepository.save(
                                    existingMember
                            );

                    return ResponseEntity.ok(saved);

                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =====================================================
    // DELETE MEMBER
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(
            @PathVariable Long id
    ) {

        if (
                !teamMemberRepository
                        .existsById(id)
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        teamMemberRepository.deleteById(id);


        return ResponseEntity.ok(
                "Team member deleted successfully"
        );
    }
}