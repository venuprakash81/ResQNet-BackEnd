
package com.example.resqnet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String qualification;
    private String specialization;
    private Integer experience;
    private String availability;

    @Column(name = "hospital_id")
    private Long hospitalId;

    public Doctor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
}
