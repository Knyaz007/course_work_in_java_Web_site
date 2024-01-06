package com.example.laba.controls;

import com.example.laba.controls.EmailService;
//import com.example.laba.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

//    @Autowired
//    private StudentRepository studentRepository; // Предполагается, что у вас есть репозиторий для работы со студентами
//   /send-email/ y-men.y-men.y-men@mail.ru
    @GetMapping("/send-email/{user-email}")
    public @ResponseBody ResponseEntity sendSimpleEmail(
            @PathVariable("user-email") String email) {
        try {
            emailService.sendSimpleEmail(
                    email,
                    "Уведомление",
                    "Данное письмо сформировано автоматически от сервиса Spring Boot (Хабалов Владимир Александрович)");
        } catch (MailException mailException) {
            return new ResponseEntity<>(
                    "Невозможно отправить почту",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return new ResponseEntity<>(
                "Письмо успешно отправлено.",
                HttpStatus.OK
        );
    }
   // @PostMapping("/send-email")
//    public @ResponseBody ResponseEntity sendEmail(
//            @RequestParam("name") String name,
//            @RequestParam("email") String email,
//            @RequestParam("message") String message) {
//
//        try {
//            // Используйте данные из формы для отправки электронной почты
//            emailService.sendSimpleEmail(email, "Обратная связь от " + name, message);
//        } catch (MailException mailException) {
//            return new ResponseEntity<>("Невозможно отправить почту", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>("Письмо успешно отправлено.", HttpStatus.OK);
//    }



//
//    В этом примере я добавил JavaScript код в строку, которая будет возвращена в ответе.
//    Когда фронтенд получит этот ответ, он выполнит этот JavaScript код, вызывая
//    SweetAlert2 с сообщением об успешной отправке или об ошибке.

   @PostMapping("/send-email")
   public @ResponseBody ResponseEntity sendEmail(
           @RequestParam("name") String name,
           @RequestParam("email") String email,
           @RequestParam("message") String message) {

       try {
           // Используйте данные из формы для отправки электронной почты
           emailService.sendSimpleEmail(email, "Обратная связь от " + name, message);

           // Покажите SweetAlert2 уведомление об успешной отправке на фронтенде
           String successMessage = "Письмо успешно отправлено разработчикам.";
           String script = String.format("Успех! %s.", successMessage);
           return new ResponseEntity<>(script, HttpStatus.OK);

       } catch (MailException mailException) {
           // Покажите SweetAlert2 уведомление об ошибке на фронтенде
           String errorMessage = "Невозможно отправить почту";
           String script = String.format("Swal.fire('Ошибка', '%s', 'error');", errorMessage);
           return new ResponseEntity<>(script, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }


}
