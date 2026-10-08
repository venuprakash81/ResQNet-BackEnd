
package com.example.resqnet.service;

import com.example.resqnet.model.Ambulance;
import com.example.resqnet.model.AmbulanceBooking;
import com.example.resqnet.repository.AmbulanceBookingRepository;
import com.example.resqnet.repository.AmbulanceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AmbulanceBookingService {

    private final AmbulanceBookingRepository bookingRepository;
    private final AmbulanceRepository ambulanceRepository;

    public AmbulanceBookingService(
            AmbulanceBookingRepository bookingRepository,
            AmbulanceRepository ambulanceRepository) {
        this.bookingRepository = bookingRepository;
        this.ambulanceRepository = ambulanceRepository;
    }

    public List<AmbulanceBooking> getAll() {
        return bookingRepository.findAll();
    }

    public List<AmbulanceBooking> getForCitizen(String email) {
        return bookingRepository
                .findByCitizenEmailIgnoreCaseOrderByCreatedAtDesc(email);
    }

    public List<AmbulanceBooking> getForHospital(Long hospitalId) {
        return bookingRepository
                .findByHospitalIdOrderByCreatedAtDesc(hospitalId);
    }

    @Transactional
    public AmbulanceBooking create(AmbulanceBooking request) {
        if (request.getHospitalId() == null ||
                request.getAmbulanceId() == null) {
            throw new IllegalArgumentException(
                    "Hospital ID and ambulance ID are required"
            );
        }

        if (blank(request.getCitizenName()) ||
                blank(request.getCitizenEmail()) ||
                blank(request.getCitizenPhone()) ||
                blank(request.getPickupLocation()) ||
                blank(request.getDestination())) {
            throw new IllegalArgumentException(
                    "Please provide all required booking details"
            );
        }

        Ambulance ambulance = ambulanceRepository
                .findById(request.getAmbulanceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ambulance not found"
                ));

        if (!request.getHospitalId().equals(ambulance.getHospitalId())) {
            throw new IllegalArgumentException(
                    "Ambulance does not belong to this hospital"
            );
        }

        if (ambulance.getAvailableAmbulances() == null ||
                ambulance.getAvailableAmbulances() <= 0 ||
                !"Active".equalsIgnoreCase(ambulance.getStatus())) {
            throw new IllegalStateException(
                    "This ambulance is currently unavailable"
            );
        }

        request.setId(null);
        request.setStatus("PENDING");

        return bookingRepository.save(request);
    }

    @Transactional
    public AmbulanceBooking confirm(Long bookingId, Long hospitalId) {
        AmbulanceBooking booking = findBooking(bookingId);
        verifyHospital(booking, hospitalId);

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only pending bookings can be confirmed"
            );
        }

        Ambulance ambulance = ambulanceRepository
                .findById(booking.getAmbulanceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ambulance not found"
                ));

        if (ambulance.getAvailableAmbulances() == null ||
                ambulance.getAvailableAmbulances() <= 0) {
            throw new IllegalStateException(
                    "No ambulances are currently available"
            );
        }

        ambulance.setAvailableAmbulances(
                ambulance.getAvailableAmbulances() - 1
        );

        ambulance.setAssignedAmbulances(
                (ambulance.getAssignedAmbulances() == null
                        ? 0 : ambulance.getAssignedAmbulances()) + 1
        );

        ambulanceRepository.save(ambulance);
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    @Transactional
    public AmbulanceBooking reject(Long bookingId, Long hospitalId) {
        AmbulanceBooking booking = findBooking(bookingId);
        verifyHospital(booking, hospitalId);

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only pending bookings can be rejected"
            );
        }

        booking.setStatus("REJECTED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public AmbulanceBooking complete(Long bookingId, Long hospitalId) {
        AmbulanceBooking booking = findBooking(bookingId);
        verifyHospital(booking, hospitalId);

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be completed"
            );
        }

        Ambulance ambulance = ambulanceRepository
                .findById(booking.getAmbulanceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ambulance not found"
                ));

        int assigned = ambulance.getAssignedAmbulances() == null
                ? 0 : ambulance.getAssignedAmbulances();

        ambulance.setAssignedAmbulances(Math.max(0, assigned - 1));

        int available = ambulance.getAvailableAmbulances() == null
                ? 0 : ambulance.getAvailableAmbulances();

        ambulance.setAvailableAmbulances(
                Math.min(
                        ambulance.getTotalAmbulances() == null
                                ? available + 1
                                : ambulance.getTotalAmbulances() - assigned + 1,
                        available + 1
                )
        );

        ambulanceRepository.save(ambulance);
        booking.setStatus("COMPLETED");

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteForCitizen(Long bookingId, String email) {
        AmbulanceBooking booking = findBooking(bookingId);

        if (booking.getCitizenEmail() == null ||
                !booking.getCitizenEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Booking does not belong to this citizen"
            );
        }

        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "A confirmed booking must be cancelled by the hospital"
            );
        }

        if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException(
                    "Completed bookings cannot be deleted"
            );
        }

        bookingRepository.delete(booking);
    }

    private AmbulanceBooking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking not found"
                ));
    }

    private void verifyHospital(
            AmbulanceBooking booking, Long hospitalId) {
        if (!hospitalId.equals(booking.getHospitalId())) {
            throw new IllegalArgumentException(
                    "Booking does not belong to this hospital"
            );
        }
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
