package com.example.laba.repository;

import com.example.laba.models.ThingsRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ThingsRoomRepository extends CrudRepository<ThingsRoom, Long> {

//     List<ThingsRoom> findByRoomId(Long id);
}

