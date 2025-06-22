package com.example.laba.repository;

import com.example.laba.models.Excursion;
import com.example.laba.models.Flight;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ExcursionRepository extends CrudRepository<Excursion, Long> {
 
   // Исправленный метод для поиска экскурсий по id тура
    List<Excursion> findByTours_TourId(Long tourId);

 
  /* List<Excursion> findByTour_TourId(Long tourId); */

  // Поиск по части названия (ignore case)
  List<Excursion> findByTitleContainingIgnoreCase(String title);

  // Поиск по части локации
  List<Excursion> findByLocationContainingIgnoreCase(String location);

  // Поиск по дате
  List<Excursion> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

  // Комбинированный поиск (опционально можно использовать @Query для гибкости)
  List<Excursion> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndDateTimeBetween(
      String title, String location, LocalDateTime start, LocalDateTime end);

  List<Excursion> findAll();


List<Excursion> findByTitleContainingIgnoreCaseAndCityContainingIgnoreCaseAndDateTimeBetween(
        String title,
        String city,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime);

    List<Excursion> findByTitleContainingIgnoreCaseAndCityContainingIgnoreCase(
        String title,
        String city);

}
