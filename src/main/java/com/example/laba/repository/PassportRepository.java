package com.example.laba.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.example.laba.models.Passport;

public interface PassportRepository extends CrudRepository<Passport, Long> {
    List<Passport> findByUser_UserId(Long userId); // Метод для поиска паспортов по userId
}  // Используем имя поля "user" и "_id" findByUser_Id(Long userId) говорит Spring Data JPA, что нужно искать по полю user, а потом по его id.
