package com.example.laba.controllers;

import com.example.laba.models.Booking;

import com.example.laba.repository.*;
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
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private   bookingRepository BookingRepository;

    @Autowired
    private flightRepository FlightRepository;
    @Autowired
    private HotelRepository HotelRepository;
    @Autowired
    private tourRepository TourRepository;
    @Autowired
    private UserRepository UserRepository;


    @GetMapping("/list")
    public String listBookings(Model model) {


        List<Booking> bookings = new ArrayList<>();
        BookingRepository.findAll().forEach(bookings::add);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);

        model.addAttribute("bookings", bookings);


        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "booking/list";
    }

    @GetMapping("/details/{bookingId}")
    public String bookingDetails(@PathVariable Long bookingId, Model model) {
        Optional<Booking> optionalBooking = BookingRepository.findById(bookingId);



        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            model.addAttribute("booking", booking);
            return "booking/details";
        } else {
            return "redirect:/bookings/list";
        }
    }

    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        return "booking/new";
    }

    @PostMapping("/new")
    public String saveBooking(@ModelAttribute Booking booking) {
        // Add validation or business logic as needed
        BookingRepository.save(booking);
        return "redirect:/bookings/list";
    }

    @GetMapping("/edit/{bookingId}")
    public String showEditForm(@PathVariable Long bookingId, Model model) {
        Optional<Booking> optionalBooking = BookingRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            model.addAttribute("booking", booking);
            return "booking/edit";
        } else {
            return "redirect:/bookings/list";
        }
    }

    @PostMapping("/edit/{bookingId}")
    public String updateBooking(@PathVariable Long bookingId, @ModelAttribute Booking updatedBooking) {
        // Add validation or business logic as needed
        updatedBooking.setBookingId(bookingId);
        BookingRepository.save(updatedBooking);
        return "redirect:/bookings/list";
    }

    @GetMapping("/delete/{bookingId}")
    public String deleteBooking(@PathVariable Long bookingId) {
        BookingRepository.deleteById(bookingId);
        return "redirect:/bookings/list";
    }
}
