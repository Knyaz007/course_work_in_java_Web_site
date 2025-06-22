package com.example.laba.repository;

import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, Long> {
    List<Room> findAll();
    List<Room> findByHotelId(Long hotelId);
}
