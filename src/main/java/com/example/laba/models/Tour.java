package com.example.laba.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Tour")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TourId")
    private Long tourId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Price")
    private double price;

    @Column(name = "StartDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "EndDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "AvailableSpots")
    private int availableSpots;

    @ManyToOne
    @JoinColumn(name = "HotelId")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "RoomId")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "TrainTicketId")
    private TrainTicket trainTicket;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

   @ManyToMany
@JoinTable(
    name = "Tour_Excursion",
    joinColumns = @JoinColumn(name = "TourId"),
    inverseJoinColumns = @JoinColumn(name = "ExcursionId")
)
private List<Excursion> excursions = new ArrayList<>();


     @ManyToMany
    @JoinTable(
        name = "Tour_Flight",
        joinColumns = @JoinColumn(name = "TourId"),
        inverseJoinColumns = @JoinColumn(name = "FlightId")
    )
    private List<Flight> flights = new ArrayList<>(); // МОЖЕТ БЫТЬ ПУСТОЙ

    @ManyToMany
@JoinTable(
    name = "Tour_TrainTicket",
    joinColumns = @JoinColumn(name = "TourId"),
    inverseJoinColumns = @JoinColumn(name = "Ticket_Id")
)
private List<TrainTicket> trainTickets = new ArrayList<>();

}
