package com.example.laba.repository;

import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;
import com.example.laba.models.Tour;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface flightRepository extends CrudRepository<Flight, Long> {
    List<Flight> findByDestinationContainingIgnoreCase(String destination);
    List<Flight> findAll();
    List<Flight> findByDepartureAndDestinationAndDepartureDate(String departure, String destination, LocalDate departureDate);
    
    List<Flight> findByDepartureAndDestination(String departure, String destination);
    List<Flight> findByDepartureContainingIgnoreCaseAndDestinationContainingIgnoreCase(String departure, String destination);

     List<Flight> findByDeparture (String departure);

      
List<Flight> findByDestination(String destination);
List<Flight> findByDepartureAndDepartureDate(String departure, LocalDate date);
List<Flight> findByDestinationAndDepartureDate(String destination, LocalDate date);


}

