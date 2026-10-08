package com.example.resqnet.service;

import com.example.resqnet.model.Volunteer;
import com.example.resqnet.repository.VolunteerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerService(
            VolunteerRepository volunteerRepository) {

        this.volunteerRepository =
                volunteerRepository;
    }

    // =========================================================
    // REGISTER VOLUNTEER
    // =========================================================

    public Volunteer registerVolunteer(
            Volunteer volunteer) {

        // Check email
        if (volunteerRepository
                .existsByEmail(volunteer.getEmail())) {

            throw new RuntimeException(
                    "An account already exists with this email."
            );
        }

        // Check phone
        if (volunteerRepository
                .existsByPhone(volunteer.getPhone())) {

            throw new RuntimeException(
                    "An account already exists with this phone number."
            );
        }

        return volunteerRepository.save(volunteer);
    }

    // =========================================================
    // LOGIN VOLUNTEER
    // =========================================================

    public Volunteer loginVolunteer(
            String email,
            String password) {

        Volunteer volunteer =
                volunteerRepository
                        .findByEmail(email)
                        .orElse(null);

        if (volunteer == null) {

            throw new RuntimeException(
                    "Volunteer email not registered"
            );
        }

        if (!volunteer.getPassword().equals(password)) {

            throw new RuntimeException(
                    "Incorrect password"
            );
        }

        return volunteer;
    }

    // =========================================================
    // GET ALL VOLUNTEERS
    // =========================================================

    public List<Volunteer> getAllVolunteers() {

        return volunteerRepository.findAll();
    }

    // =========================================================
    // GET VOLUNTEER BY ID
    // =========================================================

    public Volunteer getVolunteerById(Long id) {

        return volunteerRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Volunteer not found with id: " + id
                        )
                );
    }
}