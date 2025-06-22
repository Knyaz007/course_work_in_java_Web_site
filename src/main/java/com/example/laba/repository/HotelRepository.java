package com.example.laba.repository;


import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;

import com.example.laba.models.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
    List<Hotel> findByCityContainingIgnoreCase(String city);

    @Query("SELECT h.photos FROM Hotel h WHERE h.id = :hotelId")
    List<String> getHotelPhotosById(Long hotelId);

   /*  для поиска  */
    List<Hotel> findByCityContainingIgnoreCaseOrCountryContainingIgnoreCase(String city, String country);

    List<Hotel> findByCityIgnoreCaseOrCountryIgnoreCase(String city, String country);
    List<Hotel> findAll();


}