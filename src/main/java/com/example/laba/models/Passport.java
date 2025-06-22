package com.example.laba.models;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
 

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "passports")
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String passportNumber;

    @Column(nullable = false)
    private String issuedBy;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd") //использоавние форматирование
    private LocalDate issueDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

