package com.example.laba.controllers;

import com.example.laba.models.Booking;
import com.example.laba.models.Excursion;
import com.example.laba.models.Tour;
import com.example.laba.models.User;
import com.example.laba.repository.ExcursionRepository;
import com.example.laba.repository.UserRepository;
import com.example.laba.repository.bookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/excursions")
public class ExcursionController {


       /* @ModelAttribute, которая будет автоматически вызываться перед каждым методом контроллера. */
    @ModelAttribute
    public void addUserAttributesToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            model.addAttribute("roles", roles);
            model.addAttribute("authentication", authentication);

            boolean loggedIn = authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
            model.addAttribute("loggedIn", loggedIn);
        }
    }


    @Autowired
    private ExcursionRepository excursionRepository;

    @GetMapping("/list")
    public String listExcursions(Model model) {
          
        List<Excursion> excursions = new ArrayList<>();
        excursionRepository.findAll().forEach(excursions::add);
        model.addAttribute("excursions", excursions);
        return "excursion/excursionList";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("excursion", new Excursion());
        return "excursion/excursionNew";
    }

    @PostMapping("/new")
    public String createExcursion(@ModelAttribute Excursion excursion) {
        excursionRepository.save(excursion);
        return "excursions/excursionNew";
    }

    @GetMapping("/edit/{id}")
    public String editExcursion(@PathVariable("id") Long id, Model model) {
        Optional<Excursion> optional = excursionRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("excursion", optional.get());
            return "excursion/excursionEdit";
        }
        return "redirect:/excursions/";
    }

    @PostMapping("/edit")
public String updateExcursion(@ModelAttribute Excursion excursion) {
    excursionRepository.save(excursion);
    return "redirect:/excursions/list"; // ✅ правильный редирект перепровляет на метод
}


    @GetMapping("/delete/{id}")
    public String deleteExcursion(@PathVariable("id") Long id) {
        excursionRepository.deleteById(id);
        return "redirect:/excursions/";
    }

    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable("id") Long id, Model model) {
        
        Optional<Excursion> optional = excursionRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("excursion", optional.get());
            return "excursion/excursionDetails";
        }
        return "redirect:/excursions/";
    }

    @Autowired
    private UserRepository UserRepository;

    

    @Autowired
    private bookingRepository bookingRepository;


// Замените этот метод на ваш способ получения текущего пользователя
    private User getLoggedInUser(Principal principal) {
        if (principal != null) {
            return UserRepository.findByEmail(principal.getName()).orElse(null);
        }
        return null;
    }
@PostMapping("/bookingExcursions")
public String bookExcursion(@RequestParam("excursionId") Long excursionId, Principal principal, RedirectAttributes redirectAttributes ) {
    User loggedInUser = getLoggedInUser(principal);

    if (loggedInUser != null) {
        Optional<Excursion> excursionOpt = excursionRepository.findById(excursionId);
        if (excursionOpt.isEmpty()) {
            // обработка ошибки — экскурсия не найдена
            return "redirect:/excursions/list?error=notfound";
        }

        Excursion excursion = excursionOpt.get();

        Booking booking = new Booking();
        booking.setExcursion(excursion);              // ✅ устанавливаем объект экскурсии
        booking.setUser(loggedInUser);                // ✅ устанавливаем пользователя
        booking.setBookingDate(LocalDateTime.now());
        booking.setConfirmed(false);

        bookingRepository.save(booking);
/* ведомление через Flash Attributes  */
         redirectAttributes.addFlashAttribute("successMessage", "Экскурсия  успешно забронирован!"); 
       
        return "redirect:/";
    }

    return "redirect:/login"; // если пользователь не авторизован — редирект на логин
}




@GetMapping("/search")
public String searchExcursions(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        Model model
) {
    String titleFilter = (title != null) ? title : "";
    String cityFilter = (city != null) ? city : "";
    List<Excursion> excursions;

    if (date != null) {
        excursions = excursionRepository.findByTitleContainingIgnoreCaseAndCityContainingIgnoreCaseAndDateTimeBetween(
                titleFilter,
                cityFilter,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
    } else {
        excursions = excursionRepository.findByTitleContainingIgnoreCaseAndCityContainingIgnoreCase(titleFilter, cityFilter);
    }

    model.addAttribute("excursions", excursions);
    return "excursion/excursionList";
}



}
