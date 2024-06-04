package com.example.laba.controllers;


import com.example.laba.models.ThingsRoom;
import com.example.laba.repository.ThingsRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/things")
public class ThingsRoomController {

    @Autowired
    private ThingsRoomRepository thingsRoomRepository;

    @GetMapping("/add")
    public String showThingsRoomForm(Model model) {
        model.addAttribute("thingsRoom", new ThingsRoom());
        return "Things/things_room_form";
    }

    @PostMapping("/add")
    public String addThingsRoom(@ModelAttribute("thingsRoom") ThingsRoom thingsRoom) {
        thingsRoomRepository.save(thingsRoom);
        return "redirect:/things/listThings";

    }

    @GetMapping("/edit/{id}")
    public String showEditThingsRoomForm(@PathVariable("id") Long id, Model model) {
        ThingsRoom thingsRoom = thingsRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ThingsRoom Id:" + id));
        model.addAttribute("thingsRoom", thingsRoom);
        return "things_room_form";
    }

    @PostMapping("/edit/{id}")
    public String updateThingsRoom(@PathVariable("id") Long id, @ModelAttribute("thingsRoom") ThingsRoom thingsRoom) {
        thingsRoom.setId(id);
        thingsRoomRepository.save(thingsRoom);
        return "redirect:/things/listThings";

    }

    @GetMapping("/delete/{id}")
    public String deleteThingsRoom(@PathVariable("id") Long id) {
        thingsRoomRepository.deleteById(id);
        return "redirect:/things/listThings";
    }

    @GetMapping("listThings")
    public String listThingsRoom(Model model) {
        model.addAttribute("thingsRooms", thingsRoomRepository.findAll());

        return "Things/things_room_list";
    }
}
