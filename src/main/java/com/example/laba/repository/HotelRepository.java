package com.example.laba.repository;


import com.example.laba.models.Hotel;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
    List<Hotel> findByCityContainingIgnoreCase(String city);
}