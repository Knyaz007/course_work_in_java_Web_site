package com.example.laba.repository;

import com.example.laba.models.Booking;
import com.example.laba.models.Employee;
import org.springframework.data.repository.CrudRepository;

public interface bookingRepository extends CrudRepository<Booking, Long> {
}
