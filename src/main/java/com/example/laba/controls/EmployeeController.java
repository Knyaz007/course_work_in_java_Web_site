package com.example.laba.controls;

import com.example.laba.models.Employee;
import com.example.laba.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String listEmployees(Model model) {
        List<Employee> employees = new ArrayList<>();
        employeeRepository.findAll().forEach(employees::add);
        model.addAttribute("employees", employees);
        return "Employee/employeesList";
    }

    @GetMapping("/details/{employeeId}")
    public String employeeDetails(@PathVariable Long employeeId, Model model) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();

            // Получение фотографии сотрудника по его ID
//            Optional<byte[]> optionalPhoto = employeeRepository.findPhotoById(employeeId);
//            byte[] photo = optionalPhoto.get();
//            // Проверка наличия фотографии
//            if (optionalPhoto.isPresent()) {
//                // Помещение фотографии в модель
//                model.addAttribute("employeePhoto", photo);
//            }

            // Помещение сотрудника в модель
            model.addAttribute("employee", employee);

            return "Employee/employeeDetails"; // Предполагается, что у вас есть шаблон "employeeDetails"
        } else {
            return "redirect:/employees";
        }
    }
    @GetMapping("/photo/{employeeId}")
    @ResponseBody
    public ResponseEntity<byte[]> getEmployeePhoto(@PathVariable Long employeeId) {
        // Получите фотографию из базы данных или хранилища
        Optional<byte[]> photo2 = employeeRepository.findPhotoById(employeeId);

        byte[] photo = photo2.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Укажите правильный MIME-тип для вашего изображения

        return new ResponseEntity<>(photo, headers, HttpStatus.OK);
    }
    @GetMapping("/edit/{id}")
    public String editEmployee(Model model, @PathVariable("id") Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "Employee/editEmployee";
        } else {
            return "redirect:/employees";
        }
    }

    @PostMapping("/edit")
    public String editEmployee(@ModelAttribute Employee employee,@RequestParam("file")MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                byte[] photoBytes = file.getBytes();
                employee.setPhoto(photoBytes);
            } catch (IOException e) {
                // Handle the exception (e.g., log it) based on your application's needs
                e.printStackTrace();
            }
        }
        employeeRepository.save(employee);
        model.addAttribute("message", "Employee successfully edited");
        return "redirect:/employees/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(Model model, @PathVariable("id") Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
        }
        return "redirect:/employees/list";
    }

    @GetMapping("/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "Employee/newEmployee";
    }

    @PostMapping("/new")
    public String newEmployee(@ModelAttribute Employee employee, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] photoBytes = file.getBytes();
                employee.setPhoto(photoBytes);
            } catch (IOException e) {
                // Handle the exception (e.g., log it) based on your application's needs
                e.printStackTrace();
            }
        }


        employeeRepository.save(employee);
        return "redirect:/employees/list";
    }
}
