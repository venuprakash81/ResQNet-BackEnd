
package com.example.resqnet.controller;

import com.example.resqnet.model.Doctor;
import com.example.resqnet.model.DoctorBooking;
import com.example.resqnet.service.DoctorService;
import com.example.resqnet.service.DoctorBookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-bookings")
@CrossOrigin(origins = {
        "https://res-q-net-front-end-venu-prakash.vercel.app"
})
public class DoctorBookingController {
    private final DoctorService doctorService;
    private final DoctorBookingService bookingService;

    public DoctorBookingController(
            DoctorService doctorService,
            DoctorBookingService bookingService) {
        this.doctorService = doctorService;
        this.bookingService = bookingService;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        System.out.println("Doctors returned: " + doctors.size());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PostMapping
    public ResponseEntity<DoctorBooking> bookAppointment(
            @RequestBody DoctorBooking booking) {
        return ResponseEntity.ok(
                bookingService.bookAppointment(booking)
        );
    }

    @GetMapping("/patient")
    public ResponseEntity<List<DoctorBooking>> getPatientBookings(
            @RequestParam String email) {
        return ResponseEntity.ok(
                bookingService.getPatientBookings(email)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long id,
            @RequestParam String patientEmail) {
        bookingService.cancelBooking(id, patientEmail);
        return ResponseEntity.ok("Appointment cancelled successfully");
    }
}
