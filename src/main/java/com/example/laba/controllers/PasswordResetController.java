package com.example.laba.controllers;

import com.example.laba.models.BankCard;
import com.example.laba.models.Passport;
import com.example.laba.models.User;
import com.example.laba.repository.CardRepository;
import com.example.laba.repository.PassportRepository;
import com.example.laba.repository.UserRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

import javax.smartcardio.Card;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {
    
    @Autowired
    private UserService userService; // Сервис для работы с пользователями

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", new String());
         return "registration/forgot-password"; // Страница ввода email
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        String token = UUID.randomUUID().toString(); // Генерация токена
        userService.createPasswordResetToken(email, token);

        // Отправка письма
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
        userService.sendPasswordResetEmail(email, resetLink);

        model.addAttribute("message", "Письмо с инструкцией отправлено!");
        return "registration/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!userService.isValidToken(token)) {
            model.addAttribute("error", "Недействительный или просроченный токен.");
            return "error";
        }
        model.addAttribute("token", token);
        return "registration/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        if (!userService.isValidToken(token)) {
            model.addAttribute("error", "Недействительный или просроченный токен.");
            return "error";
        }
        userService.updatePassword(token, password);
        return "redirect:/login?resetSuccess";
    }
}

