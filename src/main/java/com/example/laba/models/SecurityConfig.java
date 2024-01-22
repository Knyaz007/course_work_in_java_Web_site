package com.example.laba.models;


import com.example.laba.controls.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;


    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/css/**","/public/**").permitAll()
//                         Разрешение доступа ко всем URL
                        .requestMatchers("http://localhost:8080/**","/public/**","/login","/","/send-email",  "/employees/**","/tours/**","/users/**","/flights/**","/employees/**","/register" ,"/tour/details/{tourId}","/hotels/new").permitAll() // Allow access to the main page without authentication
                        .requestMatchers("/students/delete/**", "/students/update/**", "/departments/new").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login

//                        .usernameParameter ("email") // Имя параметра для электронной почты
//                        .passwordParameter("password") // Имя параметра для пароля
//                        .defaultSuccessUrl("/").permitAll()

                       //  .loginPage("/login") //Можно использовать для перенаправления на свою страницу  login  в  RegistrationController


                        .defaultSuccessUrl("/").permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );
        return http.build();
    }
}
