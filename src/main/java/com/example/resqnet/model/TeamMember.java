package com.example.resqnet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String role;

    private String phone;

    private String email;

    private Integer age;

    private String gender;

    private String availability;

    private String skills;

    /*
     * This is the rescue team ID to which
     * this member belongs.
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;


    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public TeamMember() {
    }


    public TeamMember(
            String name,
            String role,
            String phone,
            String email,
            Integer age,
            String gender,
            String availability,
            String skills,
            Long teamId
    ) {
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.availability = availability;
        this.skills = skills;
        this.teamId = teamId;
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }


    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }


    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}