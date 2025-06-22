package com.example.laba.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "TrainTicket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ticket_Id")
    private Long ticketId;

    @Column(name = "TrainNumber", nullable = false)
    private String trainNumber;

    @Column(name = "DepartureStation", nullable = false)
    private String departureStation;

    @Column(name = "DepartureCity", nullable = false)
    private String departureCity;

    @Column(name = "DestinationStation", nullable = false)
    private String destinationStation;

    @Column(name = "DestinationCity", nullable = false)
    private String destinationCity;

    @Column(name = "DepartureDate", nullable = false)
    private LocalDate departureDate;

    @Column(name = "DepartureTime", nullable = false)
    private LocalTime departureTime;

    @Column(name = "ArrivalDate", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "ArrivalTime", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "Price", nullable = false)
    private double price;

    @Column(name = "WifiAvailability")
    private boolean wifiAvailability;

    @Column(name = "AvailablePaymentMethods")
    private String availablePaymentMethods;

    @Column(name = "LuggagePolicy")
    private String luggagePolicy;

    @Column(name = "PetPolicy")
    private String petPolicy;

    @OneToMany(mappedBy = "trainTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarriageInfo> carriages = new ArrayList<>();
    @ManyToMany(mappedBy = "trainTickets")
    private List<Tour> tours = new ArrayList<>();

}
