package com.example.laba.controllers;

import com.example.laba.models.User;
import com.example.laba.models.UserDetailsImpl;
import com.example.laba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

//    Метод loadUserByUsername: Этот метод вызывается Spring Security при попытке аутентификации пользователя.
//    В этом методе производится поиск пользователя по его имени (username) в репозитории UserRepository.
//    Если пользователь найден, создается объект UserDetailsImpl с использованием статического метода build,
//    который, вероятно, определен в классе
//    UserDetailsImpl. Этот объект UserDetails затем используется для проверки подлинности пользователя.


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}
