package com.example.laba.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Excursion")
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Excursion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ExcursionId")
    private Long excursionId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Location")
    private String location;

    @Column(name = "Country")
    private String country;

    @Column(name = "City")
    private String city;

    @Column(name = "DateTime")
    private LocalDateTime dateTime;

    @Column(name = "Price")
    private double price;

    // Убираем @ManyToOne Tour tour; !!!

    @ManyToMany(mappedBy = "excursions")
    private List<Tour> tours = new ArrayList<>();
}
