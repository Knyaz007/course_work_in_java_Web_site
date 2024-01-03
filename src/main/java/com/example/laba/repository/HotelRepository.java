package com.example.laba.repository;


import com.example.laba.models.Hotel;

import org.springframework.data.repository.CrudRepository;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
}