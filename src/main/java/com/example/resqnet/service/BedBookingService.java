package com.example.resqnet.service;

import com.example.resqnet.model.Bed;
import com.example.resqnet.model.BedBooking;
import com.example.resqnet.repository.BedBookingRepository;
import com.example.resqnet.repository.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BedBookingService {

    private final BedBookingRepository bookingRepository;
    private final BedRepository bedRepository;

    public BedBookingService(
            BedBookingRepository bookingRepository,
            BedRepository bedRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bedRepository = bedRepository;
    }

    public List<BedBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<BedBooking> getCitizenBookings(String email) {
        return bookingRepository
                .findByCitizenEmailOrderByBookingDateDesc(email);
    }

    public List<BedBooking> getHospitalBookings(Long hospitalId) {
        return bookingRepository
                .findByHospitalIdOrderByBookingDateDesc(hospitalId);
    }

    @Transactional
    public BedBooking createBooking(BedBooking request) {
        validateBooking(request);

        Bed bed = bedRepository.findByIdForUpdate(request.getBedId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Bed not found.")
                );

        if (!bed.getHospitalId().equals(request.getHospitalId())) {
            throw new IllegalArgumentException(
                    "Selected bed does not belong to this hospital."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(bed.getStatus())) {
            throw new IllegalStateException("This bed is not active.");
        }

        if (bed.getAvailableBeds() == null || bed.getAvailableBeds() <= 0) {
            throw new IllegalStateException(
                    "No beds are currently available."
            );
        }

        request.setId(null);
        request.setBedType(bed.getBedType());
        request.setStatus("PENDING");

        // A pending request does not occupy a bed.
        return bookingRepository.save(request);
    }

    @Transactional
    public BedBooking confirmBooking(Long bookingId) {
        BedBooking booking = getBooking(bookingId);

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only pending bookings can be confirmed."
            );
        }

        Bed bed = bedRepository.findByIdForUpdate(booking.getBedId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Bed not found.")
                );

        if (!"ACTIVE".equalsIgnoreCase(bed.getStatus())) {
            throw new IllegalStateException("This bed is not active.");
        }

        if (bed.getAvailableBeds() == null || bed.getAvailableBeds() <= 0) {
            throw new IllegalStateException(
                    "No beds are available to confirm this request."
            );
        }

        bed.setAvailableBeds(bed.getAvailableBeds() - 1);
        bed.setOccupiedBeds(
                (bed.getOccupiedBeds() == null ? 0 : bed.getOccupiedBeds())
                        + 1
        );

        booking.setStatus("CONFIRMED");

        bedRepository.save(bed);
        return bookingRepository.save(booking);
    }

    @Transactional
    public BedBooking rejectBooking(Long bookingId) {
        BedBooking booking = getBooking(bookingId);

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only pending bookings can be rejected."
            );
        }

        booking.setStatus("REJECTED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public BedBooking cancelBooking(Long bookingId, String citizenEmail) {
        if (citizenEmail == null || citizenEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Citizen email is required."
            );
        }

        BedBooking booking = getBooking(bookingId);

        if (!citizenEmail.trim().equalsIgnoreCase(
                booking.getCitizenEmail()
        )) {
            throw new IllegalArgumentException(
                    "This booking does not belong to this citizen."
            );
        }

        String status = booking.getStatus().toUpperCase();

        if (!status.equals("PENDING") && !status.equals("CONFIRMED")) {
            throw new IllegalStateException(
                    "This booking cannot be cancelled."
            );
        }

        if (status.equals("CONFIRMED")) {
            releaseBed(booking.getBedId());
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public BedBooking completeBooking(Long bookingId) {
        BedBooking booking = getBooking(bookingId);

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be completed."
            );
        }

        releaseBed(booking.getBedId());

        booking.setStatus("COMPLETED");
        return bookingRepository.save(booking);
    }

    private void releaseBed(Long bedId) {
        Bed bed = bedRepository.findByIdForUpdate(bedId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bed not found.")
                );

        int occupied = bed.getOccupiedBeds() == null
                ? 0
                : bed.getOccupiedBeds();

        int available = bed.getAvailableBeds() == null
                ? 0
                : bed.getAvailableBeds();

        if (occupied <= 0) {
            throw new IllegalStateException(
                    "Bed inventory is inconsistent: no occupied bed to release."
            );
        }

        bed.setOccupiedBeds(occupied - 1);
        bed.setAvailableBeds(available + 1);
        bedRepository.save(bed);
    }

    private BedBooking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + id
                        )
                );
    }

    private void validateBooking(BedBooking booking) {
        if (booking.getHospitalId() == null) {
            throw new IllegalArgumentException("Hospital ID is required.");
        }

        if (booking.getBedId() == null) {
            throw new IllegalArgumentException("Bed ID is required.");
        }

        if (booking.getPatientName() == null
                || booking.getPatientName().isBlank()) {
            throw new IllegalArgumentException("Patient name is required.");
        }

        if (booking.getCitizenEmail() == null
                || booking.getCitizenEmail().isBlank()) {
            throw new IllegalArgumentException("Citizen email is required.");
        }

        if (booking.getPhone() == null || booking.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }

        if (booking.getPatientAge() == null
                || booking.getPatientAge() < 0
                || booking.getPatientAge() > 120) {
            throw new IllegalArgumentException("Enter a valid patient age.");
        }

        if (booking.getGender() == null || booking.getGender().isBlank()) {
            throw new IllegalArgumentException("Gender is required.");
        }

        if (booking.getReason() == null || booking.getReason().isBlank()) {
            throw new IllegalArgumentException(
                    "Reason for admission is required."
            );
        }
    }
}