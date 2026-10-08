package com.example.resqnet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hospital_ambulances")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hospitalId;

    private String ambulanceNumber;

    private String ambulanceType;

    private String driverName;

    private String driverPhone;

    private Integer totalAmbulances;

    private Integer availableAmbulances;

    private Integer assignedAmbulances;

    private String status;

    public Ambulance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getAmbulanceNumber() {
        return ambulanceNumber;
    }

    public void setAmbulanceNumber(String ambulanceNumber) {
        this.ambulanceNumber = ambulanceNumber;
    }

    public String getAmbulanceType() {
        return ambulanceType;
    }

    public void setAmbulanceType(String ambulanceType) {
        this.ambulanceType = ambulanceType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Integer getTotalAmbulances() {
        return totalAmbulances;
    }

    public void setTotalAmbulances(Integer totalAmbulances) {
        this.totalAmbulances = totalAmbulances;
    }

    public Integer getAvailableAmbulances() {
        return availableAmbulances;
    }

    public void setAvailableAmbulances(Integer availableAmbulances) {
        this.availableAmbulances = availableAmbulances;
    }

    public Integer getAssignedAmbulances() {
        return assignedAmbulances;
    }

    public void setAssignedAmbulances(Integer assignedAmbulances) {
        this.assignedAmbulances = assignedAmbulances;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}