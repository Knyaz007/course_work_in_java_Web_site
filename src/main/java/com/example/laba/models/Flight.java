package com.example.laba.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.laba.models.enums.SeatType;
import com.example.laba.models.enums.TicketClass;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Flight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Генерация ID автоматически
    @Column(name = "Flight_Id")
    private Long flightId; // Уникальный идентификатор рейса

    @Column(name = "FlightNumber", nullable = false)
    private String flightNumber; // Номер рейса (например, SU123)

    @Column(name = "Departure", nullable = false)
    private String departure; // Город отправления

    @Column(name = "DepartureAirport", nullable = false)
    private String departureAirport; // Аэропорт отправления

    @Column(name = "Destination", nullable = false)
    private String destination; // Город назначения

    @Column(name = "ArrivalAirport", nullable = false)
    private String arrivalAirport; // Аэропорт прибытия

    @Column(name = "DepartureDate", nullable = false)
@DateTimeFormat(pattern = "yyyy-MM-dd")
private LocalDate departureDate; // Дата отправления (только дата)

    @Column(name = "DepartureTime", nullable = false)
    private LocalTime departureTime; // Время отправления (только время)

    @Column(name = "ArrivalDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate; // Дата прибытия (только дата)

    @Column(name = "ArrivalTime", nullable = false)
    private LocalTime arrivalTime; // Время прибытия (только время)

    @Column(name = "AvailableSeats", nullable = false)
    private int availableSeats; // Количество доступных мест

    @Column(name = "Price", nullable = false)
    private double price; // Стоимость билета  

    @Column(name = "FlightTime")
    private double flightTime; // Время полета в часах (например, 2.5 означает 2 часа 30 минут)
    
    @Column(name = "WiFiAvailability")
    private boolean wifiAvailability; // Есть ли Wi-Fi на борту
    
    @Column(name = "AvailablePaymentMethods")
    private String availablePaymentMethods; // Доступные методы оплаты (например, Visa, PayPal)

    @Column(name = "PetPolicy")
    private String petPolicy; // Условия провоза животных

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // Список комментариев, связанных с рейсом

   
    @ManyToMany(mappedBy = "flights")
    private List<Tour> tours = new ArrayList<>(); // также может быть пустой
}
