package com.example.resqnet.repository;

import com.example.resqnet.model.VolunteerVehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VolunteerVehicleBookingRepository
        extends JpaRepository<VolunteerVehicleBooking, Long> {

    List<VolunteerVehicleBooking> findByEmailIgnoreCaseOrderByBookingTimeDesc(
            String email
    );
}