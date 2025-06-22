package com.example.laba.controllers;

import com.example.laba.models.BankCard;
import com.example.laba.models.Booking;
import com.example.laba.models.InternationalPassport;
import com.example.laba.models.Passport;
import com.example.laba.models.User;
import com.example.laba.repository.CardRepository;
import com.example.laba.repository.InternationalPassportRepository;
import com.example.laba.repository.PassportRepository;
import com.example.laba.repository.UserRepository;
import com.example.laba.repository.bookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.smartcardio.Card;

@Controller
@RequestMapping("/users")
public class UserController {
///users/list
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PassportRepository passportRepository;
    @Autowired
    private CardRepository cardRepository;

     @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        model.addAttribute("users", users);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "User/usersList";
    }

    @GetMapping("/details/{userId}")
    public String userDetails(@PathVariable Long userId, Model model) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("user", user);
            return "User/userDetails";
        } else {
            return "redirect:/main";
        }
    }

    @Autowired
    bookingRepository bookingRepository;
    
    
    InternationalPassportRepository InternationalPassportRepository;
    

    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "redirect:/users";
        }
    
    model.addAttribute("passport",  user.get().getPassport());
    model.addAttribute("cards", user.get().getBankCards()); 
    model.addAttribute("newCard", new BankCard()); // Объект для новой карты
            

        model.addAttribute("user", user.get());
        

    // Добавляем список бронирований пользователя
    List<Booking> bookings = bookingRepository.findAllByUser(user.get());
    model.addAttribute("userBookings", bookings);
 /*    Optional<InternationalPassport> InternationalPassport = InternationalPassportRepository.findByIdUser(id);
    Optional<InternationalPassport> optionalPassport = InternationalPassportRepository.findByIdUser(id); */
InternationalPassport optionalPassport = user.get().getInternationalPassport(); 
     model.addAttribute("internationalPassport", optionalPassport);

        return "User/editUser";
    }

 // Метод для добавления новой карты
 @PostMapping("/cards/add")
public String addCard(@ModelAttribute("newCard") BankCard newCard, 
                      @RequestParam("userId") Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    newCard.setUser(user); // Привязываем карту к пользователю
    cardRepository.save(newCard); // Сохраняем карту

    return "User/editUser/" + userId;
}




    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user, Model model) {

        if (user.getPassport() != null) {
            List<Passport> existingPassport = passportRepository.findByUser_UserId(user.getUserId()); // Предполагаем, что есть метод поиска паспорта по userId
            if (!existingPassport.isEmpty()) {
                // Устанавливаем ID существующего паспорта
                user.getPassport().setId(existingPassport.get(0).getId());
                // Убедитесь, что пользователь связан с паспортом
                user.getPassport().setUser(user);  // Устанавливаем пользователя для паспорта
            } else {
                // Если паспорт новый, связываем его с пользователем
                user.getPassport().setUser(user);  // Устанавливаем пользователя для нового паспорта
            }
            Passport dssd = user.getPassport();
            passportRepository.save(user.getPassport());
        }
    
       if (user.getBankCards() != null) {
    for (BankCard card : user.getBankCards()) {
        cardRepository.save(card); // Сохраняем каждую карту
    }
}
    User dfds = user;

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user); // сохраняем пользователя
        //model.addAttribute("message", "Пользователь успешно отредактирован");
    



       /*  userRepository.save(user);
        model.addAttribute("message", "Пользователь успешно отредактирован");
        */ return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(Model model, @PathVariable("id") Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        return "redirect:/";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "User/newUser";
    }

@PostMapping("/new")
public String newUser(@ModelAttribute User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    userRepository.save(user);
    return "redirect:/users/list";
}

}
