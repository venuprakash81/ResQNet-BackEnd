
package com.example.resqnet.controller;

import com.example.resqnet.model.AmbulanceBooking;
import com.example.resqnet.service.AmbulanceBookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ambulance-bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class AmbulanceBookingController {

    private final AmbulanceBookingService service;

    public AmbulanceBookingController(AmbulanceBookingService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AmbulanceBooking>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/citizen/{email}")
    public ResponseEntity<List<AmbulanceBooking>> getForCitizen(
            @PathVariable String email) {
        return ResponseEntity.ok(service.getForCitizen(email));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<AmbulanceBooking>> getForHospital(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(service.getForHospital(hospitalId));
    }

    @PostMapping("/book")
    public ResponseEntity<?> create(
            @RequestBody AmbulanceBooking request) {
        try {
            return ResponseEntity.ok(service.create(request));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/confirm/{hospitalId}")
    public ResponseEntity<?> confirm(
            @PathVariable Long bookingId,
            @PathVariable Long hospitalId) {
        try {
            return ResponseEntity.ok(
                    service.confirm(bookingId, hospitalId)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/reject/{hospitalId}")
    public ResponseEntity<?> reject(
            @PathVariable Long bookingId,
            @PathVariable Long hospitalId) {
        try {
            return ResponseEntity.ok(
                    service.reject(bookingId, hospitalId)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/complete/{hospitalId}")
    public ResponseEntity<?> complete(
            @PathVariable Long bookingId,
            @PathVariable Long hospitalId) {
        try {
            return ResponseEntity.ok(
                    service.complete(bookingId, hospitalId)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{bookingId}/citizen/{email}")
    public ResponseEntity<?> deleteForCitizen(
            @PathVariable Long bookingId,
            @PathVariable String email) {
        try {
            service.deleteForCitizen(bookingId, email);
            return ResponseEntity.ok(
                    Map.of("message", "Booking deleted successfully")
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
