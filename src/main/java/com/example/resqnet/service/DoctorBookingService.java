package com.example.resqnet.service;

import com.example.resqnet.model.Doctor;
import com.example.resqnet.model.DoctorBooking;
import com.example.resqnet.repository.DoctorRepository;
import com.example.resqnet.repository.DoctorBookingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorBookingService {

    private final DoctorBookingRepository bookingRepository;
    private final DoctorRepository doctorRepository;

    public DoctorBookingService(
            DoctorBookingRepository bookingRepository,
            DoctorRepository doctorRepository) {
        this.bookingRepository = bookingRepository;
        this.doctorRepository = doctorRepository;
    }

    public DoctorBooking bookAppointment(DoctorBooking booking) {

        if (booking.getDoctorId() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Doctor ID is required");
        }

        Doctor doctor = doctorRepository.findById(
            booking.getDoctorId()
        ).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Doctor not found"));

        if (booking.getAppointmentDate() == null ||
            booking.getAppointmentTime() == null ||
            booking.getPatientEmail() == null ||
            booking.getPatientName() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Required fields are missing");
        }

        if (booking.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Appointment date is in the past");
        }

        boolean alreadyBooked =
            bookingRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                    booking.getDoctorId(),
                    booking.getAppointmentDate(),
                    booking.getAppointmentTime(),
                    List.of("Pending", "Confirmed")
                );

        if (alreadyBooked) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Time slot is already booked");
        }

        booking.setStatus("Pending");

        return bookingRepository.save(booking);
    }

    public List<DoctorBooking> getPatientBookings(String email) {
        return bookingRepository
            .findByPatientEmailIgnoreCaseOrderByAppointmentDateDesc(
                email
            );
    }

    @Transactional
    public void cancelBooking(Long id, String patientEmail) {
        DoctorBooking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getPatientEmail().equalsIgnoreCase(patientEmail)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Not your booking");
        }

        bookingRepository.delete(booking);
    }
}