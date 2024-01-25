package com.example.laba.controllers;

import com.example.laba.models.Tour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.laba.repository.tourRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class MainController {

    @Autowired
    private tourRepository tourRepository;
    @GetMapping("/main")
    public String mainPage() {
        return "main"; // return the name of your main page template
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

        // ЗАпрос на роль авторизированного клиента
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);
        model.addAttribute("authentication", authentication);

        // Если зарегестрирован то передавать loggedIn для отображения элементов представления ( кнопка регистрация вход)
        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);


//

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

            return "tourDetails"; // Assuming you have a "tourDetails" template
        } else {
            // Handle the case where the tourId is not valid
            return "redirect:/main"; // Redirect to the home page if the tourId is not valid
        }
    }


    @GetMapping("/feedback")
    public String feedback() {
        return "feedback";

    }
}
