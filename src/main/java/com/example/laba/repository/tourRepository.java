package com.example.laba.repository;

import com.example.laba.models.Tour;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
public interface tourRepository extends CrudRepository<Tour, Long> {
    /* ищет туры, содержащие введенный текст, игнорируя регистр. */
    List<Tour> findByNameContainingIgnoreCase(String name);
    List<Tour> findByEndDateBeforeAndIsArchivedFalse(LocalDate date);
// Чтобы фильтровать только активные туры
    List<Tour> findByIsArchivedFalse();

}
