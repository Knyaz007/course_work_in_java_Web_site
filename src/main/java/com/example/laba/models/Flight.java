package com.example.laba.models;

import jakarta.persistence.*;

//import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "Flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Flight_Id")
    private Long flightId;

    @Column(name = "FlightNumber", nullable = false)
    private String flightNumber;

    @Column(name = "Departure", nullable = false)
    private String departure;

    @Column(name = "Destination", nullable = false)
    private String destination;

    @Column(name = "DepartureDateTime", nullable = false)
    private LocalDateTime departureDateTime;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
