package com.example.resqnet.repository;

import com.example.resqnet.model.BedBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedBookingRepository
        extends JpaRepository<BedBooking, Long> {

    List<BedBooking> findByHospitalIdOrderByBookingDateDesc(
            Long hospitalId
    );

    List<BedBooking> findByCitizenEmailOrderByBookingDateDesc(
            String citizenEmail
    );

    List<BedBooking> findByBedIdAndStatusIn(
            Long bedId,
            List<String> statuses
    );

    List<BedBooking> findByHospitalIdAndStatusIgnoreCaseOrderByBookingDateDesc(
            Long hospitalId,
            String status
    );
}