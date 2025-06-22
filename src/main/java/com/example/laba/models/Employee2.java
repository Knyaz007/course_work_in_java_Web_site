package com.example.laba.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Employee2")
    public class Employee2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeId2")
    private Long employeeId2;
    @Column(name = "Name")
        private String name;
    @Column(name = "Email")
        private String email;
        // Добавьте поле для хранения пути к фотографии
        @Column(name = "photoPath")
        private String photoPath;

    public Long getEmployeeId2() {
        return employeeId2;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId2 = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    // Геттеры и сеттеры
    }


