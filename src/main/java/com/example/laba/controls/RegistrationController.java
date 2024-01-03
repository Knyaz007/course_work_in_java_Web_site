package com.example.laba.controls;

import com.example.laba.models.User;
import com.example.laba.models.UserDTO;
import com.example.laba.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {




    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registrationForm(Model model) {
        // Добавляем модель, чтобы передать данные в представление
        model.addAttribute("user", new UserDTO());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(UserDTO userDTO) {
        // Обработка регистрации пользователя
        User newUser = new User();
        newUser.setName(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // Добавьте другие необходимые поля, например, email, phone, и т.д.

        // Сохраняем пользователя в базе данных
        userRepository.save(newUser);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Check if the user is already authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // User is already authenticated, redirect to the main page or a dashboard
            return "redirect:/main"; // Update the URL accordingly
        }

        // User is not authenticated, proceed with the login logic
        return "login"; // return the name of your login page template
    }
    @GetMapping("/logout")
    public String logout() {
        // Add any additional logout logic here
        return "redirect:/login?logout"; // Redirect to the login page with a logout parameter
    }
}
