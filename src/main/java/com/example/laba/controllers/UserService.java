package com.example.laba.controllers;

 
import com.example.laba.models.User;
 
import com.example.laba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
import org.springframework.stereotype.Service;
 
import java.util.HashMap;
 
import java.util.Map;
import java.util.Optional;
 


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> resetTokens = new HashMap<>();

    @Value("${spring.mail.username}")
    private String from;

    public void createPasswordResetToken(String email, String token) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            resetTokens.put(token, email);
        }
    }

    public boolean isValidToken(String token) {
        return resetTokens.containsKey(token);
    }

    public void updatePassword(String token, String newPassword) {
        String email = resetTokens.get(token);
        if (email != null) {
            Optional<User> user = userRepository.findByEmail(email);
            user.ifPresent(u -> {
                u.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                userRepository.save(u);
                resetTokens.remove(token);
            });
        }
    }

    public void sendPasswordResetEmail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Восстановление пароля");
        message.setText("Чтобы сбросить пароль, перейдите по ссылке: " + resetLink);     
        

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Handle exception, log, or rethrow as needed
            e.printStackTrace();
        }
    }
}
