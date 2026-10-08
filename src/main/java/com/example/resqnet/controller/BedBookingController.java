package com.example.resqnet.controller;

import com.example.resqnet.model.BedBooking;
import com.example.resqnet.service.BedBookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bed-bookings")
@CrossOrigin(origins = {
        "https://res-q-net-front-end-venu-prakash.vercel.app"
})
public class BedBookingController {

    private final BedBookingService bookingService;

    public BedBookingController(BedBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BedBooking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBeds() {
        // Kept for compatibility with the earlier frontend.
        return ResponseEntity.status(HttpStatus.OK)
                .body("Use GET /api/beds/available for available bed records.");
    }

    @GetMapping("/citizen/{email}")
    public ResponseEntity<List<BedBooking>> getCitizenBookings(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(
                bookingService.getCitizenBookings(email)
        );
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<BedBooking>> getHospitalBookings(
            @PathVariable Long hospitalId
    ) {
        return ResponseEntity.ok(
                bookingService.getHospitalBookings(hospitalId)
        );
    }

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(
            @RequestBody BedBooking booking
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingService.createBooking(booking));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long bookingId
    ) {
        try {
            return ResponseEntity.ok(
                    bookingService.confirmBooking(bookingId)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId
    ) {
        try {
            return ResponseEntity.ok(
                    bookingService.rejectBooking(bookingId)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String email = body == null ? null : body.get("citizenEmail");

            return ResponseEntity.ok(
                    bookingService.cancelBooking(bookingId, email)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(
            @PathVariable Long bookingId
    ) {
        try {
            return ResponseEntity.ok(
                    bookingService.completeBooking(bookingId)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }
}
