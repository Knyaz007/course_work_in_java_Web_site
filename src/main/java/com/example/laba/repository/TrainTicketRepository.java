package com.example.laba.repository;

import com.example.laba.models.Hotel;
import com.example.laba.models.TrainTicket;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainTicketRepository extends CrudRepository<TrainTicket, Long> {

    List<TrainTicket> findByDepartureCityAndDestinationCityAndDepartureDate(
        String departureCity, String destinationCity, LocalDate departureDate
    );

    List<TrainTicket> findByDepartureCityAndDestinationCity(
        String departureCity, String destinationCity
    );

    // Найти билеты по городу отправления и дате отправления
    List<TrainTicket> findByDepartureCityAndDepartureDate(
        String departureCity, LocalDate departureDate
    );

    // Найти билеты только по городу отправления
    List<TrainTicket> findByDepartureCity(String departureCity);

    // Найти билеты по городу назначения и дате отправления
    List<TrainTicket> findByDestinationCityAndDepartureDate(
        String destinationCity, LocalDate departureDate
    );

    // Найти билеты только по городу назначения
    List<TrainTicket> findByDestinationCity(String destinationCity);

    List<TrainTicket> findAll();
}
