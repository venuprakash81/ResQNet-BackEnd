
package com.example.resqnet.controller;

import com.example.resqnet.model.VolunteerVehicleBooking;
import com.example.resqnet.repository.VolunteerVehicleBookingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteer-vehicle-bookings")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5173"
})
public class VolunteerVehicleBookingController {

    private final VolunteerVehicleBookingRepository repository;

    public VolunteerVehicleBookingController(
            VolunteerVehicleBookingRepository repository) {
        this.repository = repository;
    }

    // Create a new booking
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody VolunteerVehicleBooking booking) {

        if (booking.getVehicleType() == null ||
                booking.getVehicleType().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Vehicle type is required"));
        }

        if (booking.getCitizenName() == null ||
                booking.getCitizenName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Name is required"));
        }

        if (booking.getMobileNumber() == null ||
                !booking.getMobileNumber().matches("[0-9]{10}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Enter a valid 10-digit mobile number"));
        }

        if (booking.getEmail() == null ||
                !booking.getEmail().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Enter a valid email address"));
        }

        if (booking.getFromLatitude() == null ||
                booking.getFromLongitude() == null ||
                booking.getDestinationLatitude() == null ||
                booking.getDestinationLongitude() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Select both pickup and destination points"));
        }

        if (!validCoordinates(
                booking.getFromLatitude(),
                booking.getFromLongitude()) ||
                !validCoordinates(
                        booking.getDestinationLatitude(),
                        booking.getDestinationLongitude())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid map coordinates"));
        }

        double distance = calculateDistance(
                booking.getFromLatitude(),
                booking.getFromLongitude(),
                booking.getDestinationLatitude(),
                booking.getDestinationLongitude()
        );

        if (distance <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Pickup and destination must be different"));
        }

        booking.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        booking.setStatus("PENDING");

        VolunteerVehicleBooking saved = repository.save(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Get all bookings
    @GetMapping("/all")
    public List<VolunteerVehicleBooking> getAllBookings() {
        return repository.findAll();
    }

    // Get bookings by email
    @GetMapping("/email/{email}")
    public List<VolunteerVehicleBooking> getBookingsByEmail(
            @PathVariable String email) {
        return repository.findByEmailIgnoreCaseOrderByBookingTimeDesc(email);
    }

    // Get one booking
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Booking not found")));
    }

    // Update booking status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Status is required"));
        }

        status = status.trim().toUpperCase();

        if (!List.of("PENDING", "ACTIVE", "COMPLETED")
                .contains(status)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid booking status"));
        }

        final String newStatus = status;

        return repository.findById(id)
                .<ResponseEntity<?>>map(booking -> {
                    String currentStatus = booking.getStatus() == null
                            ? ""
                            : booking.getStatus().trim().toUpperCase();

                    // Only allow the intended status progression
                    if (currentStatus.equals("PENDING")
                            && newStatus.equals("ACTIVE")) {

                        booking.setStatus("ACTIVE");

                    } else if (currentStatus.equals("ACTIVE")
                            && newStatus.equals("COMPLETED")) {

                        booking.setStatus("COMPLETED");

                    } else if (currentStatus.equals(newStatus)) {

                        return ResponseEntity.ok(booking);

                    } else {
                        return ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Invalid status transition from "
                                                + currentStatus + " to "
                                                + newStatus
                                ));
                    }

                    return ResponseEntity.ok(repository.save(booking));
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Booking not found")));
    }

    // Edit booking
    @PutMapping("/{id}")
    public ResponseEntity<?> editBooking(
            @PathVariable Long id,
            @RequestBody VolunteerVehicleBooking request) {

        return repository.findById(id)
                .<ResponseEntity<?>>map(existing -> {

                    if (request.getCitizenName() == null ||
                            request.getCitizenName().isBlank()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Name is required"));
                    }

                    if (request.getMobileNumber() == null ||
                            !request.getMobileNumber()
                                    .matches("[0-9]{10}")) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Enter a valid 10-digit mobile number"));
                    }

                    if (request.getEmail() == null ||
                            !request.getEmail().matches(
                                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Enter a valid email"));
                    }

                    if (request.getFromLatitude() == null ||
                            request.getFromLongitude() == null ||
                            request.getDestinationLatitude() == null ||
                            request.getDestinationLongitude() == null) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Select both map locations"));
                    }

                    if (!validCoordinates(
                            request.getFromLatitude(),
                            request.getFromLongitude()) ||
                            !validCoordinates(
                                    request.getDestinationLatitude(),
                                    request.getDestinationLongitude())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Invalid map coordinates"));
                    }

                    double distance = calculateDistance(
                            request.getFromLatitude(),
                            request.getFromLongitude(),
                            request.getDestinationLatitude(),
                            request.getDestinationLongitude()
                    );

                    if (distance <= 0) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Locations must be different"));
                    }

                    existing.setVehicleType("Volunteer Van");
                    existing.setCitizenName(
                            request.getCitizenName().trim());
                    existing.setMobileNumber(
                            request.getMobileNumber().trim());
                    existing.setEmail(request.getEmail().trim());

                    existing.setFromAddress(
                            "Map-selected pickup location");
                    existing.setDestinationAddress(
                            "Map-selected destination");

                    existing.setFromLatitude(
                            request.getFromLatitude());
                    existing.setFromLongitude(
                            request.getFromLongitude());
                    existing.setDestinationLatitude(
                            request.getDestinationLatitude());
                    existing.setDestinationLongitude(
                            request.getDestinationLongitude());

                    existing.setDistanceKm(
                            Math.round(distance * 100.0) / 100.0);

                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Booking not found")));
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Booking not found"));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Booking deleted successfully"));
    }

    // Validate coordinates
    private boolean validCoordinates(
            double latitude,
            double longitude) {

        return latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180;
    }

    // Calculate distance using the Haversine formula
    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double earthRadius = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return earthRadius * c;
    }
}
