package com.example.resqnet.service;

import com.example.resqnet.model.Admin;
import com.example.resqnet.repository.AdminRepository;

import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin login(String email, String password) {

        if (email == null || password == null ||
                email.trim().isEmpty() || password.isEmpty()) {
            throw new RuntimeException(
                    "Email and password are required"
            );
        }

        Admin admin = adminRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return admin;
    }
}