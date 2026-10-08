
package com.example.resqnet.repository;

import com.example.resqnet.model.AmbulanceBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbulanceBookingRepository
        extends JpaRepository<AmbulanceBooking, Long> {

    List<AmbulanceBooking> findByCitizenEmailIgnoreCaseOrderByCreatedAtDesc(
            String citizenEmail
    );

    List<AmbulanceBooking> findByHospitalIdOrderByCreatedAtDesc(
            Long hospitalId
    );

    List<AmbulanceBooking> findByAmbulanceId(Long ambulanceId);
}
