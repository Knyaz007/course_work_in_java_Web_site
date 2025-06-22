package com.example.laba.models;

public class RoundTripTicket {
    private TrainTicket departureTicket;
    private TrainTicket returnTicket;

    public RoundTripTicket(TrainTicket departureTicket, TrainTicket returnTicket) {
        this.departureTicket = departureTicket;
        this.returnTicket = returnTicket;
    }

    public TrainTicket getDepartureTicket() {
        return departureTicket;
    }

    public TrainTicket getReturnTicket() {
        return returnTicket;
    }
}
