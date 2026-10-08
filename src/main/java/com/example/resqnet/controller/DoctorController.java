package com.example.resqnet.controller;

import com.example.resqnet.model.Doctor;
import com.example.resqnet.repository.DoctorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:5173"
})
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // GET ALL DOCTORS FOR A HOSPITAL
    @GetMapping("/{hospitalId}/doctors")
    public ResponseEntity<List<Doctor>> getDoctors(
            @PathVariable Long hospitalId) {

        List<Doctor> doctors =
                doctorRepository.findByHospitalId(hospitalId);

        return ResponseEntity.ok(doctors);
    }

    // GET ONE DOCTOR
    @GetMapping("/{hospitalId}/doctors/{doctorId}")
    public ResponseEntity<?> getDoctor(
            @PathVariable Long hospitalId,
            @PathVariable Long doctorId) {

        Optional<Doctor> optionalDoctor =
                doctorRepository.findById(doctorId);

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Doctor doctor = optionalDoctor.get();

        if (doctor.getHospitalId() == null ||
                !doctor.getHospitalId().equals(hospitalId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(doctor);
    }

    // ADD DOCTOR
    @PostMapping("/{hospitalId}/doctors")
    public ResponseEntity<?> addDoctor(
            @PathVariable Long hospitalId,
            @RequestBody Doctor doctor) {

        doctor.setId(null);
        doctor.setHospitalId(hospitalId);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedDoctor);
    }

    // UPDATE DOCTOR
    @PutMapping("/{hospitalId}/doctors/{doctorId}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable Long hospitalId,
            @PathVariable Long doctorId,
            @RequestBody Doctor updatedDoctor) {

        Optional<Doctor> optionalDoctor =
                doctorRepository.findById(doctorId);

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Doctor doctor = optionalDoctor.get();

        if (doctor.getHospitalId() == null ||
                !doctor.getHospitalId().equals(hospitalId)) {
            return ResponseEntity.badRequest()
                    .body("Doctor does not belong to this hospital");
        }

        doctor.setName(updatedDoctor.getName());
        doctor.setEmail(updatedDoctor.getEmail());
        doctor.setPhone(updatedDoctor.getPhone());
        doctor.setQualification(updatedDoctor.getQualification());
        doctor.setSpecialization(updatedDoctor.getSpecialization());
        doctor.setExperience(updatedDoctor.getExperience());
        doctor.setAvailability(updatedDoctor.getAvailability());

        Doctor savedDoctor = doctorRepository.save(doctor);

        return ResponseEntity.ok(savedDoctor);
    }

    // DELETE DOCTOR
    @DeleteMapping("/{hospitalId}/doctors/{doctorId}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long hospitalId,
            @PathVariable Long doctorId) {

        Optional<Doctor> optionalDoctor =
                doctorRepository.findById(doctorId);

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Doctor doctor = optionalDoctor.get();

        if (doctor.getHospitalId() == null ||
                !doctor.getHospitalId().equals(hospitalId)) {
            return ResponseEntity.badRequest()
                    .body("Doctor does not belong to this hospital");
        }

        doctorRepository.delete(doctor);

        return ResponseEntity.ok(
                "Doctor deleted successfully");
    }
}
