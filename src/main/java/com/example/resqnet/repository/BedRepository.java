package com.example.resqnet.repository;

import com.example.resqnet.model.Bed;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByHospitalId(Long hospitalId);

    List<Bed> findByStatusIgnoreCase(String status);

    List<Bed> findByHospitalIdAndStatusIgnoreCase(
            Long hospitalId,
            String status
    );

    List<Bed> findByAvailableBedsGreaterThanAndStatusIgnoreCase(
            Integer availableBeds,
            String status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bed b WHERE b.id = :id")
    Optional<Bed> findByIdForUpdate(@Param("id") Long id);
}