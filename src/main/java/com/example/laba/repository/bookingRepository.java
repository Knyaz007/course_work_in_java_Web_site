package com.example.laba.repository;

import com.example.laba.models.Booking;
import com.example.laba.models.Employee;
import com.example.laba.models.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface bookingRepository extends CrudRepository<Booking, Long> {
     // Найти все бронирования по ID пользователя
    
    List<Booking> findAllByUser(User user);
}
