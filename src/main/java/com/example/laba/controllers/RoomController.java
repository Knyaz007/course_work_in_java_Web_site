package com.example.laba.controllers;

import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.repository.HotelRepository;
import com.example.laba.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private  RoomRepository roomService;
    @Autowired
    private  HotelRepository hotelRepository;


    @GetMapping("/list")
    public String listRooms(Model model) {
        //hotelRepository.findAll().forEach(hotels::add);
        List<Room> room = new ArrayList<>();
        roomService.findAll().forEach(room::add);
        model.addAttribute("rooms", room);
        return "room/listRoom";
    }

    @GetMapping("/{hotelId}")
    public String getRoomsByHotel(@PathVariable("hotelId") Long hotelId, Model model) {
        List<Room> rooms = roomService.findByHotelId(hotelId);
        model.addAttribute("rooms", rooms);
        return "room/list";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute("room") Room room) {
        roomService.save(room);
        return "redirect:/rooms/listRoom";
    }
}
