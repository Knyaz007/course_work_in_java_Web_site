package com.example.laba.controls;

import com.example.laba.models.Comment;
import com.example.laba.models.Flight;

import com.example.laba.repository.flightRepository;
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
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private flightRepository flightRepository;

    @GetMapping("/list")
    public String listFlights(Model model) {
        List<Flight> flights = new ArrayList<>();
        flightRepository.findAll().forEach(flights::add);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);
        model.addAttribute("flights", flights);


        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "Flight/flightsList";
    }

    @GetMapping("/details/{flightId}")
    public String flightDetails(@PathVariable Long flightId, Model model) {
        Optional<Flight> optionalFlight = flightRepository.findById(flightId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();
            model.addAttribute("flight", flight);
            return "Flight/flightDetails"; // Assuming you have a "flightDetails" template
        } else {
            return "redirect:/flights";
        }
    }

    @GetMapping("/edit/{id}")
    public String editFlight(Model model, @PathVariable("id") Long id) {
        Optional<Flight> flight = flightRepository.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        if (flight.isPresent()) {
            model.addAttribute("flight", flight.get());
            return "Flight/editFlight";
        } else {
            return "redirect:/flights";
        }
    }

    @PostMapping("/edit")
    public String editFlight(@ModelAttribute Flight flight, Model model) {
        // Clear comments to avoid issues with orphan removal
        if (flight.getComments() != null) {
            flight.getComments().clear();
        }

        flightRepository.save(flight);
        model.addAttribute("message", "Flight successfully edited");
        return "redirect:/flights/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFlight(Model model, @PathVariable("id") Long id) {
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);
        }
        return "redirect:/flights/list";
    }

    @GetMapping("/new")
    public String newFlight(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);
        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);



        model.addAttribute("flight", new Flight());
        return "Flight/newFlight";
    }

    @PostMapping("/new")
    public String newFlight(@ModelAttribute Flight flight) {
        flightRepository.save(flight);
        return "redirect:/flights/list";
    }
}
