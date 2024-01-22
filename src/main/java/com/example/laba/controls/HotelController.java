package com.example.laba.controls;

import com.example.laba.models.Comment;
import com.example.laba.models.Hotel;
import com.example.laba.repository.HotelRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;



    @GetMapping("/list")
    public String listHotels(Model model) {
        List<Hotel> hotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotels::add);
        model.addAttribute("hotels", hotels);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);

        return "Hotel/hotelsList";
    }

    @GetMapping("/details/{hotelId}")
    public String hotelDetails(@PathVariable Long hotelId, Model model) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);



        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            model.addAttribute("hotel", hotel);

            return "Hotel/hotelDetails"; // Assuming you have a "hotelDetails" template
        } else {
            return "redirect:/hotels";
        }
    }

    @GetMapping("/edit/{id}")
    public String editHotel(Model model, @PathVariable("id") Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        if (hotel.isPresent()) {
            model.addAttribute("hotel", hotel.get());
            return "Hotel/editHotel";
        } else {
            return "redirect:/hotels";
        }
    }
    @PostMapping("/edit")
    public String editHotel(@ModelAttribute Hotel hotel,
                            @RequestParam("newPhotos") List<MultipartFile> newPhotos,
                            @RequestParam(value = "deletedPhotos", required = false) String deletedPhotoPath,
                            @RequestParam(value = "existingPhotos", required = false) List<String> existingPhotos,
                            Model model) throws IOException {
        try {
            //  Этот метод если пользователь захочет удаленть и добавить одновременно
            if (existingPhotos != null) {
                saveExitPhotos(hotel,existingPhotos);
            }

        // список удаляемых фото
        String[] deletedPhotos = deletedPhotoPath.split(",");

        // Clear comments to avoid issues with orphan removal
        if (hotel.getComments() != null) {
            hotel.getComments().clear();
        }

        // это для логов
          Logger logger = LoggerFactory.getLogger(HotelController.class);

        // Remove the specified photos from the hotel entity
        if (deletedPhotoPath != null && !deletedPhotoPath.isEmpty()) {
            deletePhotos(hotel, deletedPhotos);

            logger.info("Cleared comments...");
            logger.info("Deleted photos: {}", Arrays.toString(deletedPhotos));
            // Use deletedPhotos array as needed
        }

//            // сохранения и новых и старых фото
//            if(existingPhotos!= null && newPhotos != null) {
//                savePhotosFoEdit(hotel, newPhotos, existingPhotos);
//            }

     // newPhoto всегда не null поэтому добавляем проверку, что первый элемент не пустое имя -
        String filename ="";
        for (MultipartFile newPhoto : newPhotos) {
            filename = newPhoto.getOriginalFilename();

            // If you want to do something with the filename, you can add your logic here
        }
        // Process new photos сохраняем так если нет ранее сохраняемых фото. Так сложно ибо потому, что ты можешь изменить
        // допустим название, но по факуты ты должен перезаписать все поля потому, что выше мы перезаписываем и добавляем
        // а тут просто добаляем
        if (newPhotos != null && !newPhotos.isEmpty() && filename!="" ) {
            // Save the new photos
            savePhotos(hotel, newPhotos);
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        hotelRepository.save(hotel);
        model.addAttribute("message", "Hotel successfully edited");

        // If registered, pass loggedIn to display view elements (registration and login button)
        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);
    } catch (HotelEditException ex) {
        // Обработка исключения и передача сообщения об ошибке на страницу представления
        model.addAttribute("error", ex.getMessage());
        return "your-error-view"; // Замените на имя вашего представления с сообщением об ошибке
    }
        return "redirect:/hotels/edit/" + hotel.getId();

    }

   //Для ошибок
    public class HotelEditException extends RuntimeException {
        public HotelEditException(String message) {
            super(message);
        }
    }

//    @PostMapping("/edit")
//    public String editHotel(@ModelAttribute Hotel hotel, Model model) {
//        // Clear comments to avoid issues with orphan removal
//        if (hotel.getComments() != null) {
//            hotel.getComments().clear();
//        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        List<String> roles = authentication.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        model.addAttribute("roles", roles);
//        hotelRepository.save(hotel);
//        model.addAttribute("message", "Hotel successfully edited");
//
//        // Если зарегестрирован то передавать loggedIn для отображения элементов представления ( кнопка регистрация вход)
//        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
//
//        model.addAttribute("loggedIn", loggedIn);
//        model.addAttribute("authentication", authentication);
//
//        return "redirect:/hotels/list";
//    }
    @PersistenceContext
    private EntityManager entityManager; //для метода delete
    @Transactional
    @GetMapping("/delete/{id}")
    public String deleteHotel(Model model, @PathVariable("id") Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

//        он удалит отель вместе с связанными с ним комментариями из-за каскадного эффекта.
            Hotel hotel = entityManager.find(Hotel.class, id);
            if (hotel != null) {
                entityManager.remove(hotel);
            }




        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
        }
        return "redirect:/hotels/list";
    }

    @GetMapping("/new")
    public String newHotel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        model.addAttribute("hotel", new Hotel());

        boolean loggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("authentication", authentication);


        return "Hotel/newHotel";
    }

//    @PostMapping("/new")
//    public String newHotel(@ModelAttribute Hotel hotel) {
//        hotelRepository.save(hotel);
//
//        return "redirect:/hotels";
//    }
@PostMapping("/new")
public String createHotel(@ModelAttribute Hotel hotel, @RequestParam("photos22") List<MultipartFile> photos) throws IOException {
    if (!photos.isEmpty()) {
        // Обработка фотографий (сохранение на сервере или в базе данных)
        hotelRepository.save(hotel); // Сначала сохраняем отель
        savePhotos(hotel, photos);
    } else {
        // Обработка случая, когда нет загруженных фотографий
        // Здесь можете добавить дополнительную логику по вашему усмотрению
    }
    // Редирект на страницу отелей
    return "redirect:/hotels/list";
}

    private void savePhotos(Hotel hotel, List<MultipartFile> photos) throws IOException {
        // Получаем ID отеля
        Long hotelId = hotel.getId();
        // Создаем путь к директории для отеля внутри uploads
        // Path uploadDir = Paths.get("uploads", "hotel-" + hotelId);
        Path uploadDir = Paths.get("src", "main", "resources", "static", "uploads", "hotel-" + hotelId);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Получаем список существующих фотографий отеля
        List<String> existingPhotoNames = hotel.getPhotos();

        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(photo.getOriginalFilename()));
                //  String decodedFileName = URLDecoder.decode(fileName, "UTF-8");


                String transliteratedText = transliterate(fileName);
                System.out.println(transliteratedText);
                Path filePath = uploadDir.resolve(transliteratedText);

                // Проверка, есть ли уже фото с таким именем в базе
                if (existingPhotoNames.contains(transliteratedText)) {
                    String errorMessage = "Фото с именем " + transliteratedText + " уже существует в базе.";
                    System.out.println(errorMessage);
                    // Вызываем исключение с сообщением об ошибке
                    throw new HotelEditException(errorMessage);
                    // Обработайте этот случай по вашему усмотрению, например, пропустите сохранение

                }

                try {
                    Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    hotel.addPhoto(transliteratedText); // Сохраните имя файла в модели отеля
                    hotelRepository.save(hotel);
                } catch (IOException e) {
                    e.printStackTrace(); // Обработайте ошибку в соответствии с вашими требованиями
                }
            }
        }
    }
    private void saveExitPhotos(Hotel hotel,  List<String> photoPaths ) throws IOException {
        // Process existing photos
        for (String existingPhoto : photoPaths) {
            // You may want to validate the existing photo path here
            hotel.addPhoto(existingPhoto);
        }
        hotelRepository.save(hotel);
    }

    private void deletePhotos(Hotel hotel, String[] photoPaths) {

        String baseDirectoryфф = "src/main/resources/static/uploads/";
        try {
            // Specify the base directory where your photos are stored
            String baseDirectory = "src/main/resources/static/uploads/";

            // Iterate over each photo path in the array
            for (String photoPath : photoPaths) {
                // Combine the base directory and the relative photo path
                Path fullPath = Paths.get(baseDirectory, photoPath);

                // Delete the file if it exists
                Files.deleteIfExists(fullPath);

                // Optionally, you can also remove the photo from the hotel entity
                hotel.removePhoto(photoPath);
            }

            // Save the updated hotel entity to the repository after removing photos
            hotelRepository.save(hotel);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception according to your requirements
        }
    }

    private void savePhotosFoEdit(Hotel hotel, List<MultipartFile> newPhotos, List<String> existingPhotos) throws IOException {
        // Получаем ID отеля
        Long hotelId = hotel.getId();
        // Создаем путь к директории для отеля внутри uploads
        // Path uploadDir = Paths.get("uploads", "hotel-" + hotelId);
        Path uploadDir = Paths.get("src", "main", "resources", "static", "uploads", "hotel-" + hotelId);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Process existing photos
        for (String existingPhoto : existingPhotos) {
            // You may want to validate the existing photo path here
            hotel.addPhoto(existingPhoto);
        }

        List<String> existingPhotoNames = hotel.getPhotos();

        // Process new photos
        for (MultipartFile newPhoto : newPhotos) {
            if (!newPhoto.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(newPhoto.getOriginalFilename()));
                String decodedFileName = URLDecoder.decode(fileName, "UTF-8");

                String transliteratedText = transliterate(fileName);
                System.out.println(transliteratedText);
                Path filePath = uploadDir.resolve(transliteratedText);

                if (existingPhotoNames.contains(transliteratedText)) {
                    String errorMessage = "Фото с именем " + transliteratedText + " уже существует в базе.";
                    System.out.println(errorMessage);
                    // Вызываем исключение с сообщением об ошибке
                    throw new HotelEditException(errorMessage);
                    // Обработайте этот случай по вашему усмотрению, например, пропустите сохранение

                }


                try {
                    Files.copy(newPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    hotel.addPhoto(transliteratedText); // Сохраните имя файла в модели отеля
                } catch (IOException e) {
                    e.printStackTrace(); // Обработайте ошибку в соответствии с вашими требованиями
                }
            }
        }

        hotelRepository.save(hotel);
    }


    @GetMapping("/upload")
public String upload(Model model) {

        return "Hotel/qwe";
    }


        @PostMapping("/upload")
public String uploadPhotos(@RequestParam("photos") List<MultipartFile> photos) {
            // Поместите код для сохранения фотографий на сервере или в базе данных
            // Например, сохраните их на сервере в директории uploads

            for (MultipartFile photo : photos) {
                String fileName = photo.getOriginalFilename();
                Path filePath = Path.of("uploads", fileName);

                try {
                    Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Ваш код для сохранения имени файла или другой логики
                } catch (IOException e) {
                    e.printStackTrace(); // Обработайте ошибку в соответствии с вашими требованиями
                    return "Failed to upload photos.";
                }
            }

            return "Photos uploaded successfully.";
        }

    private String transliterate(String text) {
        text = text.replaceAll("а", "a");
        text = text.replaceAll("б", "b");
        text = text.replaceAll("в", "v");
        text = text.replaceAll("г", "g");
        text = text.replaceAll("д", "d");
        text = text.replaceAll("е", "e");
        text = text.replaceAll("ё", "e");
        text = text.replaceAll("ж", "zh");
        text = text.replaceAll("з", "z");
        text = text.replaceAll("и", "i");
        text = text.replaceAll("й", "y");
        text = text.replaceAll("к", "k");
        text = text.replaceAll("л", "l");
        text = text.replaceAll("м", "m");
        text = text.replaceAll("н", "n");
        text = text.replaceAll("о", "o");
        text = text.replaceAll("п", "p");
        text = text.replaceAll("р", "r");
        text = text.replaceAll("с", "s");
        text = text.replaceAll("т", "t");
        text = text.replaceAll("у", "u");
        text = text.replaceAll("ф", "f");
        text = text.replaceAll("х", "kh");
        text = text.replaceAll("ц", "ts");
        text = text.replaceAll("ч", "ch");
        text = text.replaceAll("ш", "sh");
        text = text.replaceAll("щ", "sch");
        text = text.replaceAll("ъ", "'");
        text = text.replaceAll("ы", "y");
        text = text.replaceAll("ь", "'");
        text = text.replaceAll("э", "e");
        text = text.replaceAll("ю", "yu");
        text = text.replaceAll("я", "ya");

        // Добавим обработку заглавных букв
        text = text.replaceAll("А", "A");
        text = text.replaceAll("Б", "B");
        text = text.replaceAll("В", "V");
        text = text.replaceAll("Г", "G");
        text = text.replaceAll("Д", "D");
        text = text.replaceAll("Е", "E");
        text = text.replaceAll("Ё", "E");
        text = text.replaceAll("Ж", "Zh");
        text = text.replaceAll("З", "Z");
        text = text.replaceAll("И", "I");
        text = text.replaceAll("Й", "Y");
        text = text.replaceAll("К", "K");
        text = text.replaceAll("Л", "L");
        text = text.replaceAll("М", "M");
        text = text.replaceAll("Н", "N");
        text = text.replaceAll("О", "O");
        text = text.replaceAll("П", "P");
        text = text.replaceAll("Р", "R");
        text = text.replaceAll("С", "S");
        text = text.replaceAll("Т", "T");
        text = text.replaceAll("У", "U");
        text = text.replaceAll("Ф", "F");
        text = text.replaceAll("Х", "Kh");
        text = text.replaceAll("Ц", "Ts");
        text = text.replaceAll("Ч", "Ch");
        text = text.replaceAll("Ш", "Sh");
        text = text.replaceAll("Щ", "Sch");
        text = text.replaceAll("Ъ", "'");
        text = text.replaceAll("Ы", "Y");
        text = text.replaceAll("Ь", "'");
        text = text.replaceAll("Э", "E");
        text = text.replaceAll("Ю", "Yu");
        text = text.replaceAll("Я", "Ya");

        return text;
    }

}


