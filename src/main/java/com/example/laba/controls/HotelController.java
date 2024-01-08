package com.example.laba.controls;

import com.example.laba.models.Comment;
import com.example.laba.models.Hotel;
import com.example.laba.repository.HotelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;



    @GetMapping("/list")
    public String listHotels(Model model) {
        List<Hotel> hotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotels::add);
        model.addAttribute("hotels", hotels);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "Hotel/hotelsList";
    }

    @GetMapping("/details/{hotelId}")
    public String hotelDetails(@PathVariable Long hotelId, Model model) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);



        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            model.addAttribute("hotel", hotel);

            return "Hotel/hotelDetails"; // Assuming you have a "hotelDetails" template
        } else {
            return "redirect:/hotels";
        }
    }

    @GetMapping("/edit/{id}")
    public String editHotel(Model model, @PathVariable("id") Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        if (hotel.isPresent()) {
            model.addAttribute("hotel", hotel.get());
            return "Hotel/editHotel";
        } else {
            return "redirect:/hotels";
        }
    }

    @PostMapping("/edit")
    public String editHotel(@ModelAttribute Hotel hotel, Model model) {
        // Clear comments to avoid issues with orphan removal
        if (hotel.getComments() != null) {
            hotel.getComments().clear();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        hotelRepository.save(hotel);
        model.addAttribute("message", "Hotel successfully edited");

        // Если зарегестрирован то передавать loggedIn для отображения элементов представления ( кнопка регистрация вход)
        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "redirect:/hotels/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteHotel(Model model, @PathVariable("id") Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
        }
        return "redirect:/hotels";
    }

    @GetMapping("/new")
    public String newHotel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        model.addAttribute("hotel", new Hotel());

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        return "Hotel/newHotel";
    }

    @PostMapping("/new")
    public String newHotel(@ModelAttribute Hotel hotel) {
        hotelRepository.save(hotel);

        return "redirect:/hotels";
    }
}
