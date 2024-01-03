package com.example.laba.repository;

import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;

import org.springframework.data.repository.CrudRepository;

public interface flightRepository extends CrudRepository<Flight, Long> {
}