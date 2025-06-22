package com.example.laba.controllers;

import com.example.laba.models.Booking;
import com.example.laba.models.CarriageInfo;
import com.example.laba.models.Excursion;
import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.models.Tour;
import com.example.laba.models.TrainTicket;
import com.example.laba.models.User;
import com.example.laba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//  http://localhost:8080/tours/new
@Controller
@RequestMapping("/tours")
public class TourController {
    /*
     * @ModelAttribute, которая будет автоматически вызываться перед каждым методом
     * контроллера.
     */
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
    private tourRepository tourRepository;

        @Autowired
    private HotelRepository HotelRepository;
    @Autowired
    private RoomRepository RoomlRepository;
    @Autowired
    private flightRepository  FlightsRepository ;
    @Autowired
    private ExcursionRepository ExcursionRepository;
    @Autowired
    private TrainTicketRepository TrainTicketRepository;
    @Autowired
    private CarriageInfoRepository CarriageInfoRepository;

    @GetMapping("/search")
    public String searchTours(@RequestParam(name = "tour", required = false) String tourName, Model model) {
        List<Tour> tours = (tourName != null && !tourName.isEmpty())
                ? tourRepository.findByNameContainingIgnoreCase(tourName)
                : (List<Tour>) tourRepository.findAll();

        model.addAttribute("tours", tours);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);
        model.addAttribute("authentication", authentication);

        boolean loggedIn = authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser");
        model.addAttribute("loggedIn", loggedIn);
        return "Tour/tourList"; // Укажи правильное название Thymeleaf-шаблона
    }

    @GetMapping("/list")
public String listTours(Model model, Authentication authentication) {
    List<Tour> tours;

    boolean isAdmin = false;

    if (authentication != null) {
        isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    if (isAdmin) {
        tours = new ArrayList<>();
        tourRepository.findAll().forEach(tours::add);
    } else {
        tours = tourRepository.findByIsArchivedFalse();
    }

    model.addAttribute("tours", tours);
    return "Tour/tourList";
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
        // Передача модели в представление
        // model.addAttribute("user", new User()); // Пустой объект User для формы
        // регистрации
        model.addAttribute("tours", tours);
        /* Здесь нужно получить список туров из сервиса или репозитория */;

        // Если зарегестрирован то передавать loggedIn для отображения элементов
        // представления ( кнопка регистрация вход)
        boolean loggedIn = authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);

        return "main"; // Имя представления (шаблона Thymeleaf)
    }

   @GetMapping("/details/{tourId}")
public String tourDetails(@PathVariable Long tourId, Model model, RedirectAttributes redirectAttributes) {
    Optional<Tour> optionalTour = tourRepository.findById(tourId);

    if (optionalTour.isPresent()) {
        Tour tour = optionalTour.get();
        model.addAttribute("tour", tour);
        return "Tour/tourDetails"; // шаблон находится в папке templates/Tour/tourDetails.html
    } else {
        redirectAttributes.addFlashAttribute("error", "Тур с указанным ID не найден.");
        return "redirect:/main";
    }
}

    // http://localhost:8080/tours/edit/1
 @GetMapping("/edit/{id}")
public String editTourForm(@PathVariable Long id, Model model) {
    Tour tour = tourRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid tour Id:" + id));

    List<Hotel> hotels = HotelRepository.findAll();
    List<Room> rooms = RoomlRepository.findAll();
    List<TrainTicket> trainTickets = TrainTicketRepository.findAll();
    List<Flight> flights = FlightsRepository.findAll();
    List<Excursion> excursions = ExcursionRepository.findAll();  // если нужно

         List<CarriageInfo> listOfCarriageInfo = new ArrayList<>();
         CarriageInfoRepository.findAll().forEach(listOfCarriageInfo::add);

model.addAttribute("carriages", listOfCarriageInfo);
    model.addAttribute("tour", tour);
    model.addAttribute("hotels", hotels);
    model.addAttribute("rooms", rooms);
    model.addAttribute("trainTickets", trainTickets); // соответствие с th:each="trainTicket : ${trainTickets}"
    model.addAttribute("flights", flights);
    model.addAttribute("excursions", excursions); // если есть экскурсии

    return "Tour/editTour"; // имя thymeleaf шаблона
}


@PostMapping("/edit/{id}")
public String updateTour(
        @PathVariable Long id,
        @RequestParam Long hotelId,
        @RequestParam Long roomId,
        @RequestParam(required = false) List<Long> flightId,
        @RequestParam(required = false) List<Long> trainTicketId,
        @ModelAttribute Tour tour
) {
    // Находим существующий тур
    Tour existingTour = tourRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid tour Id: " + id));

    // Обновляем основные поля
    existingTour.setName(tour.getName());
    existingTour.setDescription(tour.getDescription());
    existingTour.setPrice(tour.getPrice());
    existingTour.setStartDate(tour.getStartDate());
    existingTour.setEndDate(tour.getEndDate());
    existingTour.setAvailableSpots(tour.getAvailableSpots());
    existingTour.setIsArchived(tour.getIsArchived());

    // Устанавливаем отель и комнату
    Hotel hotel = HotelRepository.findById(hotelId).orElse(null);
    Room room = RoomlRepository.findById(roomId).orElse(null);
    existingTour.setHotel(hotel);
    existingTour.setRoom(room);

    // Устанавливаем список билетов на поезд
    if (trainTicketId != null) {
        Iterable<TrainTicket> ticketsIterable = TrainTicketRepository.findAllById(trainTicketId);
        List<TrainTicket> tickets = new ArrayList<>();
        ticketsIterable.forEach(tickets::add);
        existingTour.setTrainTickets(tickets);
    } else {
        existingTour.setTrainTickets(new ArrayList<>());
    }

    // Устанавливаем список перелётов
    if (flightId != null) {
        Iterable<Flight> flightsIterable = FlightsRepository.findAllById(flightId);
        List<Flight> flights = new ArrayList<>();
        flightsIterable.forEach(flights::add);
        existingTour.setFlights(flights);
    } else {
        existingTour.setFlights(new ArrayList<>());
    }

    // Сохраняем изменения
    tourRepository.save(existingTour);

    return "redirect:/tours/list"; // или на другую нужную страницу
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

        List<Hotel> listOfHotels = new ArrayList<>();
        HotelRepository.findAll().forEach(listOfHotels::add);
        
        List<Room> listOfRooms = new ArrayList<>();
        RoomlRepository.findAll().forEach(listOfRooms::add);
         

          List<Flight> listOfFlights = new ArrayList<>();
         FlightsRepository.findAll().forEach(listOfFlights::add);


            List<Excursion> listOfExcursions = new ArrayList<>();
         ExcursionRepository.findAll().forEach(listOfExcursions::add);

         List<TrainTicket> listOfTrainTickets = new ArrayList<>();
         TrainTicketRepository.findAll().forEach(listOfTrainTickets::add);
         
        List<CarriageInfo> listOfCarriageInfo = new ArrayList<>();
         CarriageInfoRepository.findAll().forEach(listOfCarriageInfo::add);
        

        model.addAttribute("hotels", listOfHotels);
        model.addAttribute("rooms", listOfRooms);
        model.addAttribute("flights", listOfFlights);
        model.addAttribute("trainTickets", listOfTrainTickets);

model.addAttribute("carriages", listOfCarriageInfo);


        model.addAttribute("excursions", listOfExcursions);


        /*
         * 
         * model.addAttribute("flights", listOfFlights.findAll());
         * model.addAttribute("trainTickets", listOfTrainTickets.findAll());
         * 
         */
        model.addAttribute("tour", new Tour());

        return "Tour/newTour";
    }


@PostMapping("/new")
public String newTour(
        @RequestParam Long hotelId,
        @RequestParam Long roomId,
        @RequestParam(required = false) List<Long> flightId,
        @RequestParam(required = false) List<Long> trainTicketId,
        @RequestParam(required = false) List<Long> excursionId,
        @ModelAttribute Tour tour
) {
    Hotel hotel = HotelRepository.findById(hotelId).orElse(null);
    Room room = RoomlRepository.findById(roomId).orElse(null);
    tour.setHotel(hotel);
    tour.setRoom(room);

    if (trainTicketId != null) {
        List<TrainTicket> tickets = new ArrayList<>();
        TrainTicketRepository.findAllById(trainTicketId).forEach(tickets::add);
        tour.setTrainTickets(tickets);
    }

    if (flightId != null) {
        List<Flight> flights = new ArrayList<>();
        FlightsRepository.findAllById(flightId).forEach(flights::add);
        tour.setFlights(flights);
    }

  if (excursionId != null) {
    List<Excursion> excursions = new ArrayList<>();
    ExcursionRepository.findAllById(excursionId).forEach(excursions::add);
    tour.setExcursions(excursions);
}
 

    tourRepository.save(tour);
    return "redirect:/";
}


    // Метод для обработки забронирования тура
    @Autowired
    private bookingRepository BookingRepository;

    // Другие методы контроллера

   @PostMapping("/bookingTour")
public String bookTour(@RequestParam("tourId") Long tourId,
                       @RequestParam("action") String action,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {
    if ("book".equals(action)) {
        User loggedInUser = getLoggedInUser(principal);

        if (loggedInUser != null) {
            Optional<Tour> tourOpt = tourRepository.findById(tourId);
            if (tourOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Тур не найден");
                return "redirect:/main";
            }

            Tour tour = tourOpt.get();

            Booking booking = new Booking();
            booking.setTour(tour);
            booking.setUser(loggedInUser);
            booking.setBookingDate(LocalDateTime.now());
            booking.setConfirmed(false);

            BookingRepository.save(booking); // ✅ с маленькой буквы

            redirectAttributes.addFlashAttribute("success", "Тур успешно забронирован!");
            return "redirect:/details/" + tourId;
        }
    }

    return "redirect:/details/" + tourId;
}


    @Autowired
    private UserRepository UserRepository;

    // Замените этот метод на ваш способ получения текущего пользователя
    private User getLoggedInUser(Principal principal) {
        if (principal != null) {
            User ds = UserRepository.findByName(principal.getName()).orElse(null);
            return UserRepository.findByName(principal.getName()).orElse(null);
        }
        return null;
    }

    /*
     * private String getLoggedInUserId() {
     * Authentication authentication =
     * SecurityContextHolder.getContext().getAuthentication();
     * if (authentication != null && authentication.isAuthenticated()) {
     * // Предположим, что ваш объект пользователя реализует UserDetails
     * UserDetails userDetails =
     * (UserDetails) authentication.getPrincipal();
     * 
     * // Здесь вы можете получить айди пользователя
     * return userDetails.getUsername(); // или getId(), в зависимости от вашей
     * структуры UserDetails
     * }
     * 
     * return null;
     * }
     */

}