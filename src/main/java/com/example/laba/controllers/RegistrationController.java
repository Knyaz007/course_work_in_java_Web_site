package com.example.laba.controllers;

import com.example.laba.models.User;
import com.example.laba.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {




    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @GetMapping("/register")
//    public String registrationForm(Model model) {
//        // Добавляем модель, чтобы передать данные в представление
//        model.addAttribute("user", new UserDTO());
//        return "registration/registration";
//    }
//
//    @PostMapping("/register")
//    public String registerUser(UserDTO userDTO) {
//        // Обработка регистрации пользователя
//        User newUser = new User();
//        newUser.setName(userDTO.getUsername());
//        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        newUser.setLastName(userDTO.getLastName());
//        newUser.setYear(userDTO.getYear());
//        newUser.setEmail(userDTO.getEmail());
//        newUser.setPhone(userDTO.getPhone());
////        newUser.setPhoto(userDTO.getPhoto());
////        newUser.setAdmin(userDTO.isAdmin());
////        newUser.setRoles(userDTO.getRoles());
//        // Сохраняем пользователя в базе данных
//        userRepository.save(newUser);
//
//        return "redirect:/login";
//    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        // Добавляем модель, чтобы передать данные в представление
        model.addAttribute("user", new User());
        return "registration/registration";
    }

@PostMapping("/register")
public String registerUser(User user, Model model) {
    // Проверка на существование email
    Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
    if (optionalUser.isPresent()) {
        model.addAttribute("error", "Пользователь с таким email уже существует.");
        return "registration/registration";
    }
    Optional<User> optionalUser2 = userRepository.findByPhone(user.getPhone());
    // Проверка на существование номера телефона
    if (optionalUser2.isPresent()) {
        model.addAttribute("error", "Пользователь с таким номером телефона уже существует.");
        return "registration/registration";
    }

    // Установка роли по умолчанию, если не задана
    if (user.getRoles() == null || user.getRoles().isBlank()) {
        user.setRoles("USER");
    }

    // Шифруем пароль
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Сохраняем пользователя в базу данных
    userRepository.save(user);

    return "redirect:/login";
}


  @GetMapping("/login")
public String login(@RequestParam(value = "redirect", required = false) String redirect,
                    Authentication authentication,
                    Model model) {
    if (authentication != null && authentication.isAuthenticated()) {
        return "redirect:/"; // Уже авторизован
    }
    model.addAttribute("redirect", redirect); // передаём redirect в форму логина
    return "login"; // шаблон login.html
}


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Add any additional logout logic here
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/";
    }
}
