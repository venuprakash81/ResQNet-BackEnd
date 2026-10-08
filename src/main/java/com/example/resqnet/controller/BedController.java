package com.example.resqnet.controller;

import com.example.resqnet.model.Bed;
import com.example.resqnet.service.BedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5173"
})
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds() {
        return ResponseEntity.ok(bedService.getAllBeds());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        return ResponseEntity.ok(bedService.getAvailableBeds());
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Bed>> getBedsByHospital(
            @PathVariable Long hospitalId
    ) {
        return ResponseEntity.ok(
                bedService.getBedsByHospital(hospitalId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bed> getBedById(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.getBedById(id));
    }

    @PostMapping
    public ResponseEntity<?> addBed(@RequestBody Bed bed) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bedService.addBed(bed));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBed(
            @PathVariable Long id,
            @RequestBody Bed bed
    ) {
        try {
            return ResponseEntity.ok(
                    bedService.updateBed(id, bed)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBed(@PathVariable Long id) {
        try {
            bedService.deleteBed(id);
            return ResponseEntity.ok("Bed deleted successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}