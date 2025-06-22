package com.example.laba.controllers;

import com.example.laba.models.Booking;
import com.example.laba.models.Flight;
import com.example.laba.models.FlightImportUtil;
import com.example.laba.models.User;
import com.example.laba.repository.UserRepository;
import com.example.laba.repository.bookingRepository;
import com.example.laba.repository.flightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flights")
public class FlightController {

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
    private flightRepository flightRepository;

    @GetMapping("/search")
    public String searchRoundTripFlights(
        @RequestParam(name = "origin", required = false) String departure,
        @RequestParam(name = "destination", required = false) String destination,
        @RequestParam(name = "departureDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
        @RequestParam(name = "arrivalDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
        Model model
) {
    List<Flight> departureFlights = new ArrayList<>();
    List<Flight> returnFlights = new ArrayList<>();
    departure = departure.trim();
    destination = destination.trim();
   if ((departure != null && !departure.trim().isEmpty()) || (destination != null && !destination.trim().isEmpty())) {
    departure = (departure != null) ? departure.trim() : null;
    destination = (destination != null) ? destination.trim() : null;

    // Поиск рейсов туда
    if (departure != null && destination != null && !departure.isEmpty() && !destination.isEmpty()) {
        if (departureDate != null) {
            departureFlights = flightRepository.findByDepartureAndDestinationAndDepartureDate(
                departure, destination, departureDate
            );
        } else {
            departureFlights = flightRepository.findByDepartureAndDestination(
                departure, destination
            );
        }

        // Поиск обратных рейсов
        if (arrivalDate != null) {
            returnFlights = flightRepository.findByDepartureAndDestinationAndDepartureDate(
                destination, departure, arrivalDate
            );
        } else {
            returnFlights = flightRepository.findByDepartureAndDestination(
                destination, departure
            );
        }
    } else if (departure != null && !departure.isEmpty()) {
        // Ищем только рейсы из точки отправления
        if (departureDate != null) {
            departureFlights = flightRepository.findByDepartureAndDepartureDate(
                departure, departureDate
            );
        } else {
            departureFlights = flightRepository.findByDeparture(departure);
        }
    } else if (destination != null && !destination.isEmpty()) {
        // Ищем только рейсы к месту назначения
        if (arrivalDate != null) {
            returnFlights = flightRepository.findByDestinationAndDepartureDate(
                destination, arrivalDate
            );
        } else {
            returnFlights = flightRepository.findByDestination(destination);
        }
    }
}

    // Сформировать список билетов туда-обратно
    List<RoundTripFlight> roundTrips = new ArrayList<>();
    for (Flight dep : departureFlights) {
        for (Flight ret : returnFlights) {
            roundTrips.add(new RoundTripFlight(dep, ret));
        }
    }

    model.addAttribute("roundTrips", roundTrips);

   


    // Если есть только рейсы туда или только обратно, тоже выводим
    List<Flight> flights = new ArrayList<>();
    if (roundTrips.isEmpty()) {
        if (!departureFlights.isEmpty()) {
            flights.addAll(departureFlights);
        } else if (!returnFlights.isEmpty()) {
            flights.addAll(returnFlights);
        }
         model.addAttribute("flights", flights);
         return "Flight/flightsList";

    }

    
    model.addAttribute("flights", flights);

    return "Flight/flightsListSearch";
}




    @Autowired
    private bookingRepository BookingRepository;

    
   @PostMapping("/bookingFlight")
public String bookFlight(@RequestParam("flightId") Long flightId, @RequestParam("action") String action, Principal principal, RedirectAttributes redirectAttributes )  {
    if ("book".equals(action)) {
        User loggedInUser = getLoggedInUser(principal);

        if (loggedInUser != null) {
            Optional<Flight> flightOpt = flightRepository.findById(flightId);
            if (flightOpt.isEmpty()) {
                // Обработка ошибки — рейс не найден
                return "redirect:/error";
            }

            Booking booking = new Booking();
            booking.setFlight(flightOpt.get());
            booking.setUser(loggedInUser);
            booking.setBookingDate(LocalDateTime.now());
            booking.setConfirmed(false);

            BookingRepository.save(booking);
            redirectAttributes.addFlashAttribute("successMessage", "Авиабилет успешно забронирован!"); 
       
            return "redirect:/";
        }
    }
    return "flight-details"; // или другая страница с деталями рейса
}


    @PostMapping("/bookingRoundFlight")
public String bookRoundTrip(
    @RequestParam("departureFlightId") Long departureFlightId,
    @RequestParam("returnFlightId") Long returnFlightId,
    Principal principal, RedirectAttributes redirectAttributes ) {

    User loggedInUser = getLoggedInUser(principal);
    if (loggedInUser == null) {
        return "redirect:/login";
    }

    Optional<Flight> departureFlightOpt = flightRepository.findById(departureFlightId);
    Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);

    if (departureFlightOpt.isEmpty() || returnFlightOpt.isEmpty()) {
        // Можно редирект на страницу с ошибкой или показать сообщение
        return "redirect:/error";
    }

    Flight departureFlight = departureFlightOpt.get();
    Flight returnFlight = returnFlightOpt.get();

    Booking bookingDeparture = new Booking();
    bookingDeparture.setFlight(departureFlight);
    bookingDeparture.setUser(loggedInUser);
    bookingDeparture.setBookingDate(LocalDateTime.now());
    bookingDeparture.setConfirmed(false);

    Booking bookingReturn = new Booking();
    bookingReturn.setFlight(returnFlight);
    bookingReturn.setUser(loggedInUser);
    bookingReturn.setBookingDate(LocalDateTime.now());
    bookingReturn.setConfirmed(false);

    BookingRepository.save(bookingDeparture);
    BookingRepository.save(bookingReturn);
    redirectAttributes.addFlashAttribute("successMessage", "Билеты забронированы успешно!"); 
       
    return "redirect:/";
}



    @Autowired
    private UserRepository UserRepository;
    // Замените этот метод на ваш способ получения текущего пользователя
    private User getLoggedInUser(Principal principal) {
        if (principal != null) {
            return UserRepository.findByEmail(principal.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping("/import")
    public String showImportPage() {
        return "Flight/importFlights";
    }

    @PostMapping("/import")
    public String importFlights(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Пожалуйста, выберите CSV-файл.");
            return "importFlights";
        }

        try {
            // Сохраняем временный файл
            File tempFile = File.createTempFile("flights", ".csv");
            file.transferTo(tempFile);

            // Импортируем данные
            List<Flight> flights = FlightImportUtil.importFlightsFromCSV(tempFile.getAbsolutePath());
            flightRepository.saveAll(flights);

            model.addAttribute("message", "Успешно импортировано " + flights.size() + " рейсов!");
        } catch (IOException e) {
            model.addAttribute("message", "Ошибка при обработке файла: " + e.getMessage());
        }

        return "Flight/importFlights";
    }

    @GetMapping("/list")
    public String listFlights(Model model) {
        List<Flight> flights = new ArrayList<>();
        flightRepository.findAll().forEach(flights::add);
      
        model.addAttribute("flights", flights);


   
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
