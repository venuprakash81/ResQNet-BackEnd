package com.example.resqnet.service;

import com.example.resqnet.model.Bed;
import com.example.resqnet.repository.BedBookingRepository;
import com.example.resqnet.repository.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BedService {

    private final BedRepository bedRepository;
    private final BedBookingRepository bookingRepository;

    public BedService(
            BedRepository bedRepository,
            BedBookingRepository bookingRepository
    ) {
        this.bedRepository = bedRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    public List<Bed> getAvailableBeds() {
        return bedRepository
                .findByAvailableBedsGreaterThanAndStatusIgnoreCase(
                        0, "ACTIVE"
                );
    }

    public List<Bed> getBedsByHospital(Long hospitalId) {
        return bedRepository.findByHospitalId(hospitalId);
    }

    public Bed getBedById(Long id) {
        return bedRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bed not found: " + id)
                );
    }

    @Transactional
    public Bed addBed(Bed bed) {
        validateBed(bed);

        bed.setId(null);
        bed.setStatus(
                bed.getStatus() == null || bed.getStatus().isBlank()
                        ? "ACTIVE"
                        : bed.getStatus().toUpperCase()
        );

        bed.setOccupiedBeds(0);
        bed.setAvailableBeds(bed.getTotalBeds());

        return bedRepository.save(bed);
    }

    @Transactional
    public Bed updateBed(Long id, Bed updated) {
        validateBed(updated);

        Bed existing = getBedById(id);

        int occupied = existing.getOccupiedBeds() == null
                ? 0
                : existing.getOccupiedBeds();

        if (updated.getTotalBeds() < occupied) {
            throw new IllegalArgumentException(
                    "Total beds cannot be less than occupied beds."
            );
        }

        existing.setBedType(updated.getBedType());
        existing.setWard(updated.getWard());
        existing.setTotalBeds(updated.getTotalBeds());
        existing.setAvailableBeds(updated.getTotalBeds() - occupied);
        existing.setStatus(
                updated.getStatus() == null || updated.getStatus().isBlank()
                        ? existing.getStatus()
                        : updated.getStatus().toUpperCase()
        );

        return bedRepository.save(existing);
    }

    @Transactional
    public void deleteBed(Long id) {
        Bed bed = getBedById(id);

        List< String > activeStatuses = List.of("PENDING", "CONFIRMED");

        boolean hasActiveBookings =
                !bookingRepository.findByBedIdAndStatusIn(
                        id, activeStatuses
                ).isEmpty();

        if (hasActiveBookings) {
            throw new IllegalStateException(
                    "Cannot delete a bed with pending or confirmed bookings."
            );
        }

        bedRepository.delete(bed);
    }

    private void validateBed(Bed bed) {
        if (bed.getHospitalId() == null) {
            throw new IllegalArgumentException("Hospital ID is required.");
        }

        if (bed.getBedType() == null || bed.getBedType().isBlank()) {
            throw new IllegalArgumentException("Bed type is required.");
        }

        if (bed.getTotalBeds() == null || bed.getTotalBeds() < 1) {
            throw new IllegalArgumentException(
                    "Total beds must be at least 1."
            );
        }

        if (bed.getWard() == null || bed.getWard().isBlank()) {
            throw new IllegalArgumentException("Ward is required.");
        }
    }
} 
    

