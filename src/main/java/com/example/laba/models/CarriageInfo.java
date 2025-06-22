package com.example.laba.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "CarriageInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarriageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarriageInfo_Id")
    private Long carriageInfoId;

    @Column(name = "Type")
    private String type; // Например, "Плацкарт", "Купе"

    @Column(name = "AvailableSeats")
    private int availableSeats; // Количество мест в этом вагоне

    @Column(name = "MinPrice")
    private double minPrice;

    @ManyToOne
    @JoinColumn(name = "TrainTicket_Id")
    private TrainTicket trainTicket;

    @Column(name = "SeatType")
    private String seatType;

    @Column(name = "TicketClass")
    private String ticketClass;
}
