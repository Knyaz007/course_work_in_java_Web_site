package com.example.laba.controllers;

import com.example.laba.models.Flight;
/* Класс-обёртка RoundTripFlight
Создай вспомогательный класс для хранения пары рейсов и общей стоимости: */
public class RoundTripFlight {
    private Flight departure;
    private Flight returnFlight;
    private double totalPrice;

    public RoundTripFlight(Flight departure, Flight returnFlight) {
        this.departure = departure;
        this.returnFlight = returnFlight;
        this.totalPrice = departure.getPrice() + returnFlight.getPrice();
    }

    public Flight getDeparture() {
        return departure;
    }

    public Flight getReturnFlight() {
        return returnFlight;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

