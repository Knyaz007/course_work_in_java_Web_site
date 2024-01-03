package com.example.laba.controls;

import com.example.laba.models.Booking;

import com.example.laba.models.Hotel;
import com.example.laba.repository.bookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private   bookingRepository BookingRepository;


    @GetMapping("/list")
    public String listBookings(Model model) {


        List<Booking> bookings = new ArrayList<>();
        BookingRepository.findAll().forEach(bookings::add);
        model.addAttribute("bookings", bookings);
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