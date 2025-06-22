package com.example.laba.repository;

import com.example.laba.models.Tour;
import com.example.laba.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email); // войство с именем name// войство с именем name
    Optional<User> findByPhone(String phone); 
} 

