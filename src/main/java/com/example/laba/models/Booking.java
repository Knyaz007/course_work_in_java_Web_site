package com.example.laba.models;

import jakarta.persistence.*;

//import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingId")
    private Long bookingId;

    @Column(name = "TourId")
    private Long tourId;

    @Column(name = "UserId")
    private Long userId;

    @Column(name = "FlightId")
    private Long flightId;

    @Column(name = "HotelId")
    private Long hotelId;

    @Column(name = "ParticipantsCount")
    private int participantsCount;

    @Column(name = "BookingDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;

    @Column(name = "IsConfirmed")
    private boolean isConfirmed;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
