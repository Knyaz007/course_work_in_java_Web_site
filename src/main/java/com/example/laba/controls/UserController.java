package com.example.laba.controls;

import com.example.laba.models.User;
import com.example.laba.repository.UserRepository;
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
@RequestMapping("/users")
public class UserController {
///users/list
    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "redirect:/users";
        }
        model.addAttribute("user", user.get());
        return "User/editUser";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user, Model model) {
        userRepository.save(user);
        model.addAttribute("message", "Пользователь успешно отредактирован");
        return "redirect:/";
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
        userRepository.save(user);
        return "redirect:/";
    }
}
