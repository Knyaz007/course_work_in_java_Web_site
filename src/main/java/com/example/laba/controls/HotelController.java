package com.example.laba.controls;

import com.example.laba.models.Comment;
import com.example.laba.models.Hotel;
import com.example.laba.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return "Hotel/hotelsList";
    }

    @GetMapping("/details/{hotelId}")
    public String hotelDetails(@PathVariable Long hotelId, Model model) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);

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

        hotelRepository.save(hotel);
        model.addAttribute("message", "Hotel successfully edited");
        return "redirect:/hotels";
    }

    @GetMapping("/delete/{id}")
    public String deleteHotel(Model model, @PathVariable("id") Long id) {
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
        }
        return "redirect:/hotels";
    }

    @GetMapping("/new")
    public String newHotel(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "Hotel/newHotel";
    }

    @PostMapping("/new")
    public String newHotel(@ModelAttribute Hotel hotel) {
        hotelRepository.save(hotel);
        return "redirect:/hotels";
    }
}
