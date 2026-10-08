
package com.example.resqnet.repository;

import com.example.resqnet.model.DoctorBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorBookingRepository
        extends JpaRepository<DoctorBooking, Long> {

    List<DoctorBooking>
    findByPatientEmailIgnoreCaseOrderByAppointmentDateDesc(
            String patientEmail
    );

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            List<String> statuses
    );
}
