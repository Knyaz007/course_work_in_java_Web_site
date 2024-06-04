package com.example.laba.controllers;

import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.repository.HotelRepository;
import com.example.laba.repository.RoomRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @GetMapping("/add")
    public String showRoomForm(Model model,  HttpSession session) {
        Long hotelId = (Long) session.getAttribute("hotelId");
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        Hotel hotel = hotelOptional.get();
        System.out.println(hotel.getId() +"----------------Roomsssssssssssss--------------------");
        model.addAttribute("hotels",hotel );
        model.addAttribute("room", new Room());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("authentication", authentication);
        return "Room/room_form";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute Room room,
                          @RequestParam("photos23") List<MultipartFile> photos,
                          @RequestParam("id") Long id) throws IOException {
        Optional<Hotel> hotel = hotelRepository.findById(id);

        Room room2 =room;
        room2.setHotel(hotel.get());
        if (!photos.isEmpty()) {
            // Обработка фотографий (сохранение на сервере или в базе данных)
            roomRepository.save(room2); // Сначала сохраняем отель
            savePhotos(hotel.get(), room2, photos);
        } else {
            // Обработка случая, когда нет загруженных фотографий
            // Здесь можете добавить дополнительную логику по вашему усмотрению
        }
        roomRepository.save(room2);
        // Перенаправление на метод для отображения списка комнат
//        return "redirect:/rooms/listRooms";
        return "redirect:/hotels/list";
    }
    private void savePhotos(Hotel hotel, Room room, List<MultipartFile> photos) throws IOException {
        // Получаем ID отеля
        Long hotelId = hotel.getId();
        Long roomlId = room.getId();
        // Создаем путь к директории для отеля внутри uploads
        // Path uploadDir = Paths.get("uploads", "hotel-" + hotelId);
        Path uploadDir = Paths.get("src", "main", "resources", "static", "uploads", "hotel-" + hotelId, "room-" +roomlId );
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Получаем список существующих фотографий комнаты
        List<String> existingPhotoNames = room.getPhotos();

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
                    throw new RoomController.RoomEditException(errorMessage);
                    // Обработайте этот случай по вашему усмотрению, например, пропустите сохранение

                }

                try {
                    Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    room.addPhoto(transliteratedText); // Сохраните имя файла в модели комнаты
                    roomRepository.save(room);
                } catch (IOException e) {
                    e.printStackTrace(); // Обработайте ошибку в соответствии с вашими требованиями
                }
            }
        }
    }
    public class RoomEditException extends RuntimeException {
        public RoomEditException(String message) {
            super(message);
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

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") Long id, Model model)  {
        Optional<Room> room = roomRepository.findById(id);

        model.addAttribute("room", room);
        model.addAttribute("hotelRoom", room.get().getHotel());
        model.addAttribute("hotels", hotelRepository.findAll());

        return "Room/room_form_edit";
    }

    @PostMapping("/edit")
    public String updateRoom(@ModelAttribute("room") Room room)  throws IOException{
        //room.setId(id);
        roomRepository.save(room);
        // Перенаправление на метод для отображения списка комнат
        return "redirect:/rooms/listRooms";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            Long hotelId = room.getHotel().getId();
            Long roomId = room.getId();
            // Удаление комнаты из базы данных
            roomRepository.deleteById(id);

            // Удаление папки с фотографиями комнаты
            try {
                Path uploadDir = Paths.get("src", "main", "resources", "static", "uploads", "hotel-" + hotelId, "room-" + roomId);
                Files.walk(uploadDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace(); // Обработайте ошибку в соответствии с вашими требованиями
            }
        } else {
            // Обработка случая, когда комната не найдена
            // Можно добавить логику по вашему усмотрению
        }

        // Перенаправление на метод для отображения списка комнат
        return "redirect:/rooms/listRooms";
    }


    @GetMapping("listRooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        return "Room/room_list";
    }
}
