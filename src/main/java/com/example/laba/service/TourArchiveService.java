package com.example.laba.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.laba.models.Booking;
import com.example.laba.models.Excursion;
import com.example.laba.models.Flight;
import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.models.Tour;
import com.example.laba.models.TrainTicket;
import com.example.laba.models.User;
import com.example.laba.repository.UserRepository;
import com.example.laba.repository.bookingRepository;
import com.example.laba.repository.tourRepository;
import com.example.laba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.laba.service.*;

import jakarta.annotation.PostConstruct;

@Service
public class TourArchiveService {

    @Autowired
    private tourRepository tourRepository;

    @PostConstruct
    public void testRun() {
        archiveOldTours(); // автоматический запуск после старта - полу костыль.
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // каждый день в 00:00
    public void archiveOldTours() {
        List<Tour> expiredTours = tourRepository.findByEndDateBeforeAndIsArchivedFalse(LocalDate.now());
        for (Tour tour : expiredTours) {
            tour.setIsArchived(true);
        }
        tourRepository.saveAll(expiredTours);

    }
    
}
