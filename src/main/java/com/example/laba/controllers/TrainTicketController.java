package com.example.laba.controllers;

import com.example.laba.models.Booking;
import com.example.laba.models.CarriageInfo;
import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.models.TrainTicket;
import com.example.laba.models.User;
import com.example.laba.models.RoundTripTicket;
import com.example.laba.repository.TrainTicketRepository;
import com.example.laba.repository.UserRepository;
import com.example.laba.repository.bookingRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.example.laba.repository.CarriageInfoRepository;

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
@RequestMapping("/tickets")
public class TrainTicketController {

    @Autowired
    private TrainTicketRepository trainTicketRepository;

    @Autowired
    private CarriageInfoRepository carriageInfoRepository;

    

    @Autowired
    private bookingRepository BookingRepository;

    // Добавление данных пользователя в модель
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

    
@GetMapping("/search")
public String searchTickets(
    @RequestParam(name = "origin", required = false) String departureCity,
    @RequestParam(name = "destination", required = false) String destinationCity,
    @RequestParam(name = "departureDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
    @RequestParam(name = "arrivalDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
    Model model
) {
    List<TrainTicket> departureTickets = new ArrayList<>();
    List<TrainTicket> returnTickets = new ArrayList<>();

    // Обрезаем пробелы
    if (departureCity != null) departureCity = departureCity.trim();
    if (destinationCity != null) destinationCity = destinationCity.trim();

    boolean hasDeparture = departureCity != null && !departureCity.isEmpty();
    boolean hasDestination = destinationCity != null && !destinationCity.isEmpty();

    if (hasDeparture && hasDestination) {
        // Поиск билетов туда
        if (departureDate != null) {
            departureTickets = trainTicketRepository.findByDepartureCityAndDestinationCityAndDepartureDate(
                departureCity, destinationCity, departureDate);
        } else {
            departureTickets = trainTicketRepository.findByDepartureCityAndDestinationCity(
                departureCity, destinationCity);
        }

        // Поиск обратных билетов (меняем города местами)
        if (arrivalDate != null) {
            returnTickets = trainTicketRepository.findByDepartureCityAndDestinationCityAndDepartureDate(
                destinationCity, departureCity, arrivalDate);
        } else {
            returnTickets = trainTicketRepository.findByDepartureCityAndDestinationCity(
                destinationCity, departureCity);
        }
    } else if (hasDeparture) {
        // Только билеты из города отправления
        if (departureDate != null) {
            departureTickets = trainTicketRepository.findByDepartureCityAndDepartureDate(departureCity, departureDate);
        } else {
            departureTickets = trainTicketRepository.findByDepartureCity(departureCity);
        }
    } else if (hasDestination) {
        // Только билеты в город назначения
        if (arrivalDate != null) {
            returnTickets = trainTicketRepository.findByDestinationCityAndDepartureDate(destinationCity, arrivalDate);
        } else {
            returnTickets = trainTicketRepository.findByDestinationCity(destinationCity);
        }
    }

    // Формируем пары туда-обратно, если есть и те, и другие
    List<RoundTripTicket> roundTrips = new ArrayList<>();
    if (!departureTickets.isEmpty() && !returnTickets.isEmpty()) {
        for (TrainTicket dep : departureTickets) {
            for (TrainTicket ret : returnTickets) {
                roundTrips.add(new RoundTripTicket(dep, ret));
            }
        }
    }

    if (!roundTrips.isEmpty()) {
        model.addAttribute("roundTrips", roundTrips);
        return "TrainTicket/ticketsSearch";
    }

    // Если нет пар туда-обратно, показываем просто доступные билеты
    List<TrainTicket> tickets = new ArrayList<>();
    if (!departureTickets.isEmpty()) {
        tickets.addAll(departureTickets);
    } else if (!returnTickets.isEmpty()) {
        tickets.addAll(returnTickets);
    }

    if (!tickets.isEmpty()) {
        model.addAttribute("tickets", tickets);
         return "TrainTicket/ticketsList";
    }

    // Если ничего не найдено, просто возвращаем страницу поиска
    return "TrainTicket/ticketsSearch";
}


@PostMapping("/bookingRoundTicket")
public String bookRoundTrip(
    @RequestParam("departureTicketId") Long departureTicketId,
    @RequestParam("returnTicketId") Long returnTicketId,
    @RequestParam("departureCarriageIds") Long departureCarriageId,
    @RequestParam("returnCarriageIds") Long returnCarriageId,
    Principal principal,
    RedirectAttributes redirectAttributes 
    ) {

    User loggedInUser = getLoggedInUser(principal);
    if (loggedInUser == null) {
        return "redirect:/login";
    }

    Optional<TrainTicket> departureTicketOpt = trainTicketRepository.findById(departureTicketId);
    Optional<TrainTicket> returnTicketOpt = trainTicketRepository.findById(returnTicketId);
    Optional<CarriageInfo> departureCarriageOpt = carriageInfoRepository.findById(departureCarriageId);
    Optional<CarriageInfo> returnCarriageOpt = carriageInfoRepository.findById(returnCarriageId);

    if (departureTicketOpt.isEmpty() || returnTicketOpt.isEmpty() ||
        departureCarriageOpt.isEmpty() || returnCarriageOpt.isEmpty()) {
        return "redirect:/error";
    }

    TrainTicket departureTicket = departureTicketOpt.get();
    TrainTicket returnTicket = returnTicketOpt.get();
    CarriageInfo departureCarriage = departureCarriageOpt.get();
    CarriageInfo returnCarriage = returnCarriageOpt.get();

    Booking bookingDeparture = new Booking();
    bookingDeparture.setTrainTicket(departureTicket);
    bookingDeparture.setUser(loggedInUser);
    bookingDeparture.setBookingDate(LocalDateTime.now());
    bookingDeparture.setConfirmed(false);
    bookingDeparture.setCarriageInfo(departureCarriage); // если такое поле есть

    Booking bookingReturn = new Booking();
    bookingReturn.setTrainTicket(returnTicket);
    bookingReturn.setUser(loggedInUser);
    bookingReturn.setBookingDate(LocalDateTime.now());
    bookingReturn.setConfirmed(false);
    bookingReturn.setCarriageInfo(returnCarriage); // если такое поле есть

    BookingRepository.save(bookingDeparture);
    BookingRepository.save(bookingReturn);

    /* ведомление через Flash Attributes  */
    redirectAttributes.addFlashAttribute("successMessage", "Билет успешно забронирован!"); 
       

    return "redirect:/";
}




 @PostMapping("/bookingTickets")
public String bookTour(@RequestParam("carriageInfoId") Long carriageInfoId, 
@RequestParam Long ticketTrain, RedirectAttributes redirectAttributes, 
 Principal principal) {
    // Получение текущего пользователя
    User loggedInUser = getLoggedInUser(principal);

    if (loggedInUser != null) {
        // Получаем объект CarriageInfo по ID из базы
        Optional<CarriageInfo> carriageInfoOpt = carriageInfoRepository.findById(carriageInfoId);
        Optional<TrainTicket> TrainTicketOpt = trainTicketRepository.findById(ticketTrain);
        if (carriageInfoOpt.isEmpty()) {
            return "redirect:/error"; // Вагон не найден
        }

        CarriageInfo carriageInfo = carriageInfoOpt.get();
        TrainTicket trainTicket = TrainTicketOpt.get();
        // Создаем объект бронирования
        Booking booking = new Booking();
        booking.setTrainTicket(trainTicket);
        booking.setCarriageInfo(carriageInfo); // ❗ устанавливаем объект, а не ID
        booking.setUser(loggedInUser);
        booking.setBookingDate(LocalDateTime.now());
        booking.setConfirmed(false); // можно изменить по логике

        // Сохраняем бронирование
        BookingRepository.save(booking); // с маленькой буквы — имя поля


/* ведомление через Flash Attributes  */
        redirectAttributes.addFlashAttribute("successMessage", "Билет успешно забронирован!"); 
        return "redirect:/";
    }

    // Если пользователь не найден
    return "redirect:/login";
}

 

    // Показать список всех билетов
    @GetMapping("/list")
    public String listTickets(Model model) {
        List<TrainTicket> tickets = (List<TrainTicket>) trainTicketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "TrainTicket/ticketsList";
    }

    
    @GetMapping("/details/{ticketId}")
public String viewTicket(@PathVariable Long ticketId,
                         Model model,
                         HttpServletRequest request) {
    Optional<TrainTicket> optionalTicket = trainTicketRepository.findById(ticketId);

    if (optionalTicket.isPresent()) {
        TrainTicket ticket = optionalTicket.get();
        List<CarriageInfo> carriages = ticket.getCarriages();

        model.addAttribute("ticket", ticket);
        model.addAttribute("carriages", carriages);

        // Сформировать текущий URL для возврата после логина
       // Текущий URL с query string (если есть)
        String currentUrl = request.getRequestURI() +
            (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        model.addAttribute("currentUrl", currentUrl);

        return "TrainTicket/ticketsDetails";
    } else {
        model.addAttribute("error", "Билет с ID " + ticketId + " не найден");
        return "TrainTicket/ticketsDetails";
    }
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

    

@GetMapping("/add")
public String showAddTicketForm(Model model) {
     model.addAttribute("trainTicket", new TrainTicket()); // пустой объект для заполнения в форме
    return "TrainTicket/ticketsAdd"; // имя thymeleaf-шаблона с формой добавления
}

 
    public TrainTicketController(TrainTicketRepository trainTicketRepository, CarriageInfoRepository carriageInfoRepository) {
        this.trainTicketRepository = trainTicketRepository;
        this.carriageInfoRepository = carriageInfoRepository;
    }

    @PostMapping("/add")
    public String addTrainTicket(@ModelAttribute TrainTicket trainTicket) {
        TrainTicket savedTicket = trainTicketRepository.save(trainTicket);
        return "redirect:/tickets/" + savedTicket.getTicketId() + "/carriages/add";
    }

    @GetMapping("/{ticketId}/carriages/add")
    public String showAddCarriageForm(@PathVariable Long ticketId, Model model) {
        CarriageInfo carriageInfo = new CarriageInfo();
        model.addAttribute("carriageInfo", carriageInfo);
        model.addAttribute("ticketId", ticketId);

        // Получаем список уже добавленных вагонов, чтобы отображать на странице
        List<CarriageInfo> carriages = carriageInfoRepository.findByTrainTicketTicketId(ticketId);
        model.addAttribute("carriages", carriages);

        return "TrainTicket/carriage_add"; 
    }

    @PostMapping("/{ticketId}/carriages/add")
    public String addCarriage(@PathVariable Long ticketId,
                             @ModelAttribute CarriageInfo carriageInfo) {
        TrainTicket trainTicket = trainTicketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));

        carriageInfo.setTrainTicket(trainTicket);
        carriageInfoRepository.save(carriageInfo);

        // Остаёмся на той же странице, чтобы добавить ещё один вагон
        return "redirect:/tickets/" + ticketId + "/carriages/add";
    }


    // Показ формы редактирования
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        TrainTicket ticket = trainTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID билета: " + id));
        model.addAttribute("trainTicket", ticket);
        return "TrainTicket/ticketsEdit"; // имя шаблона Thymeleaf (edit-ticket.html)
    }

    // Обработка сохранения изменений
    @PostMapping("/edit")
    public String updateTicket(@ModelAttribute("trainTicket") TrainTicket ticket) {
        trainTicketRepository.save(ticket); // сохраняет обновлённый билет
        /* return "redirect:/tickets"; */ // перенаправление на список билетов (или другую страницу)
        return "redirect:/tickets/list";
    }


}
