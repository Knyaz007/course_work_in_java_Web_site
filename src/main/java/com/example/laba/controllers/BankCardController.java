package com.example.laba.controllers;

import com.example.laba.models.BankCard;
import com.example.laba.models.User;
import com.example.laba.repository.CardRepository;
import com.example.laba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cards")
public class BankCardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // Форма добавления новой карты
    @GetMapping("/add")
    public String addCardForm(Model model) {
        model.addAttribute("card", new BankCard()); // создаем пустую карту
        return "cards/add"; // Возвращаем шаблон для добавления карты
    }

    // Обработка формы добавления карты
    @PostMapping("/add")
    public String addCard(@ModelAttribute BankCard card,  @ModelAttribute User user) {
        // Сохраняем новую карту в базу данных
       cardRepository.save(card);
        return "User/editUser"; // Перенаправляем на список карт после добавления
    }

    // Форма редактирования карты
    @GetMapping("/edit/{id}")
    public String editCard(@PathVariable Long id, Model model) {
        Optional<BankCard> card = cardRepository.findById(id);
        if (card.isPresent()) {
            model.addAttribute("card", card.get());
            return "cards/edit";
        }
        return "redirect:/cards/list";
    }

    // Обновление данных карты
    @PostMapping("/update/{id}")
    public String updateCard(@PathVariable Long id, @ModelAttribute BankCard updatedCard) {
        Optional<BankCard> cardOptional = cardRepository.findById(id);
        if (cardOptional.isPresent()) {
            BankCard card = cardOptional.get();
            card.setCardNumber(updatedCard.getCardNumber());
            card.setCardHolder(updatedCard.getCardHolder());
            card.setExpiryDate(updatedCard.getExpiryDate());
            card.setBankName(updatedCard.getBankName());
            cardRepository.save(card);
        }
        return "redirect:/cards/list";
    }

    // Удаление карты
    @GetMapping("/delete/{id}")
    public String deleteCard(@PathVariable Long id) {
        cardRepository.deleteById(id);
        return "redirect:/cards/list";
    }
}
