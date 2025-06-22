package com.example.laba.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.laba.models.enums.TicketClass;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "Booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingId")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TourId")
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FlightId")
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HotelId")
    private Hotel hotel;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TrainTicketId")
    private TrainTicket trainTicket;

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomId")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarriageInfoId")
    private CarriageInfo carriageInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ExcursionId")
    private Excursion excursion;

    @Column(name = "ParticipantsCount")
    private int participantsCount;

    @Column(name = "BookingDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime bookingDate;

    @Column(name = "IsConfirmed")
    private boolean isConfirmed;

    @PrePersist
    public void prePersist() {
        if (this.bookingDate == null) {
            this.bookingDate = LocalDateTime.now();
        }
    }
}
