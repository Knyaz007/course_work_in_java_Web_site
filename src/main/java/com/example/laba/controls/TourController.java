package com.example.laba.controls;

import com.example.laba.models.Tour;

import com.example.laba.repository.tourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//  http://localhost:8080/tours/new
@Controller
@RequestMapping("/tours")
public class TourController {

    @Autowired
    private tourRepository tourRepository;
    // @PreAuthorize("hasRole('USER')")
    @GetMapping("/tours")
    public String listTours(Model model) {
        List<Tour> tours = new ArrayList<>();
        tourRepository.findAll().forEach(tours::add);
        model.addAttribute("tours", tours);
        return "toursList";
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Tour> tours = new ArrayList<>();
        tourRepository.findAll().forEach(tours::add);

        // Получение атрибута из модели, установленного в методе editTour
        String message = (String) model.getAttribute("message");
        if (message != null) {
            // Добавить сообщение в модель для использования в представлении
            model.addAttribute("message", message);
        }
        // Передача модели в представление
        //  model.addAttribute("user", new User()); // Пустой объект User для формы регистрации
        model.addAttribute("tours", tours); /* Здесь нужно получить список туров из сервиса или репозитория */;

        return "main"; // Имя представления (шаблона Thymeleaf)
    }

    @GetMapping("details/{tourId}")
    public String tourDetails(@PathVariable Long tourId, Model model) {
        // Logic to fetch tour details by tourId, you may want to check if the tourId is valid
        Optional<Tour> optionalTour = tourRepository.findById(tourId);

        if (optionalTour.isPresent()) {
            Tour tour = optionalTour.get();
            model.addAttribute("tour", tour);

            return "Tour/tourDetails"; // Assuming you have a "tourDetails" template
        } else {
            // Handle the case where the tourId is not valid
            return "redirect:/main"; // Redirect to the home page if the tourId is not valid
        }
    }

//    @GetMapping("/details/{id}")
//    public String details(Model model, @PathVariable("id") Long id) {
//        Optional<Tour> optionalTour = tourRepository.findById(id);
//        if (optionalTour.isEmpty()) {
//            return "redirect:/tours";
//        }
//        model.addAttribute("selectedTour", optionalTour.get());
//        return "tourDetails";
//    }
    //  http://localhost:8080/tours/edit/1
    @GetMapping("/edit/{id}")
    public String editTour(Model model, @PathVariable("id") Long id) {
        Optional<Tour> tour = tourRepository.findById(id);
        if (tour.isEmpty()) {
            return "redirect:/tours";
        }
        model.addAttribute("tour", tour.get());
        return "Tour/editTour";
    }

    @PostMapping("/edit")
    public String editTour(@ModelAttribute Tour tour,Model model) {
        // Проверить, что список комментариев не является null перед вызовом clear()
        if (tour.getComments() != null) {
            tour.getComments().clear();
        }
        tourRepository.save(tour);
        // Добавить атрибут в модель для использования в методе home
        model.addAttribute("message", "Тур успешно отредактирован"); // Пример сообщения

        return "redirect:/"; // Перенаправление на метод home
    }

    @GetMapping("/delete/{id}")
    public String deleteTour(Model model, @PathVariable("id") Long id) {
        if (tourRepository.existsById(id)) {
            tourRepository.deleteById(id);
        }
        return "redirect:/";
    }

    @GetMapping("/new")
    public String newTour(Model model) {
        model.addAttribute("tour", new Tour());
        return "Tour/newTour";
    }

    @PostMapping("/new")
    public String newTour(@ModelAttribute Tour tour) {
        tourRepository.save(tour);
        return "redirect:/";
    }
}