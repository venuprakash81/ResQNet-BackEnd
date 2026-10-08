package com.example.resqnet.repository;

import com.example.resqnet.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmailIgnoreCase(String email);
}