package com.example.laba.controllers;

import com.example.laba.models.Hotel;
import com.example.laba.models.Room;
import com.example.laba.models.Service;
import com.example.laba.models.ThingsRoom;
import com.example.laba.repository.CommentRepository;
import com.example.laba.repository.HotelRepository;
import com.example.laba.repository.RoomRepository;
import com.example.laba.repository.ThingsRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ThingsRoomRepository thingsRoomRepository;
    @Autowired
    private CommentRepository commentRepository;

    /**
     * Добавляет в модель общие атрибуты пользователя (аутентификация, роли,
     * loggedIn),
     * вызывается автоматически перед каждым методом контроллера.
     */
    @ModelAttribute
    public void addUserAttributesToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            model.addAttribute("roles", roles);
            model.addAttribute("authentication", authentication);

            boolean loggedIn = authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
            model.addAttribute("loggedIn", loggedIn);
        }
    }

    /**
     * Возвращает список услуг (ThingsRoom) для всех комнат отеля по его id.
     */
    public List<ThingsRoom> getRoomsAndServicesByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ThingsRoom> roomsWithServices = new ArrayList<>();
        for (Room room : rooms) {
            List<ThingsRoom> services = room.getThingsRoom();
            if (services != null) {
                roomsWithServices.addAll(services);
            }
        }
        return roomsWithServices;
    }

    @GetMapping("/list")
    public String listHotels(Model model) {
        List<Hotel> hotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotels::add);

        model.addAttribute("hotels", hotels);

        // Карта услуг для каждого отеля
        Map<Long, List<Service>> hotelServicesMap = new HashMap<>();
        for (Hotel hotel : hotels) {
            List<Service> services = hotel.getServices();
            hotelServicesMap.put(hotel.getId(), services != null ? services : Collections.emptyList());
        }
        model.addAttribute("hotelServicesMap", hotelServicesMap);

        // Средняя оценка для каждого отеля
        Map<Long, Double> avgEval = new HashMap<>();
        for (Hotel hotel : hotels) {
            Double avg = commentRepository.findAverageEvaluationByHotelId(hotel.getId());
            avgEval.put(hotel.getId(), avg != null ? avg : 0.0);
        }
        model.addAttribute("averageEvaluation", avgEval);

        // Сервисы ThingsRoom для каждого отеля
        List<List<ThingsRoom>> thingsRoom = new ArrayList<>();
        for (Hotel hotel : hotels) {
            thingsRoom.add(getRoomsAndServicesByHotelId(hotel.getId()));
        }
        model.addAttribute("services", thingsRoom);

        // Самые дешёвые комнаты по каждому отелю
        List<Room> cheapestRooms = new ArrayList<>();
        for (Hotel hotel : hotels) {
            List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
            rooms.stream()
                    .min(Comparator.comparingDouble(Room::getPrice))
                    .ifPresent(cheapestRooms::add);
        }
        model.addAttribute("rooms", cheapestRooms);

        return "Hotel/hotelsList";
    }

    @GetMapping("/photos/{hotelId}")
    public ResponseEntity<List<String>> getHotelPhotos(@PathVariable Long hotelId) {
        List<String> photos = hotelRepository.getHotelPhotosById(hotelId);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/details/{hotelId}")
    public String hotelDetails(HttpSession session, @PathVariable Long hotelId, Model model) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        if (!optionalHotel.isPresent()) {
            return "redirect:/hotels";
        }

        Hotel hotel = optionalHotel.get();
        model.addAttribute("hotel", hotel);

        Double avgEval = commentRepository.findAverageEvaluationByHotelId(hotelId);
        model.addAttribute("averageEvaluation", avgEval);

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        model.addAttribute("rooms", rooms);

        session.setAttribute("hotelId", hotelId);

        return "Hotel/hotelDetails";
    }

    @GetMapping("/edit/{id}")
    public String editHotel(Model model, @PathVariable("id") Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (!hotel.isPresent()) {
            return "redirect:/hotels";
        }
        model.addAttribute("hotel", hotel.get());
        return "Hotel/editHotel";
    }

    @PostMapping("/edit")
    public String editHotel(@ModelAttribute Hotel hotel,
            @RequestParam("newPhotos") List<MultipartFile> newPhotos,
            @RequestParam(value = "deletedPhotos", required = false) String deletedPhotoPath,
            @RequestParam(value = "existingPhotos", required = false) List<String> existingPhotos,
            Model model) throws IOException {
        Logger logger = LoggerFactory.getLogger(HotelController.class);
        try {
            // Обработка сохранённых фото
            if (existingPhotos != null && !existingPhotos.isEmpty()) {
                saveExitPhotos(hotel, existingPhotos);
            }

            // Обработка удалённых фото
            if (deletedPhotoPath != null && !deletedPhotoPath.isEmpty()) {
                String[] deletedPhotos = deletedPhotoPath.split(",");
                deletePhotos(hotel, deletedPhotos);
                logger.info("Deleted photos: {}", Arrays.toString(deletedPhotos));
            }

            // Проверяем новые фото - добавляем только если есть не пустое имя файла
            boolean hasNewPhotos = newPhotos != null &&
                    newPhotos.stream().anyMatch(f -> f != null && !f.getOriginalFilename().isEmpty());
            if (hasNewPhotos) {
                savePhotos(hotel, newPhotos);
            }

            // Очистка комментариев, чтобы избежать проблем с orphan removal
            if (hotel.getComments() != null) {
                hotel.getComments().clear();
            }

            hotelRepository.save(hotel);
            model.addAttribute("message", "Hotel successfully edited");
        } catch (HotelEditException ex) {
            model.addAttribute("error", ex.getMessage());
            return "your-error-view"; // Укажите корректное имя страницы с ошибкой
        }

        return "redirect:/hotels/edit/" + hotel.getId();
    }

    // Для ошибок
    public class HotelEditException extends RuntimeException {
        public HotelEditException(String message) {
            super(message);
        }
    }

    @PersistenceContext
    private EntityManager entityManager; // для метода delete

    @Transactional
    @GetMapping("/delete/{id}")
    public String deleteHotel(Model model, @PathVariable("id") Long id) {

        // он удалит отель вместе с связанными с ним комментариями из-за каскадного
        // эффекта.
        Hotel hotel = entityManager.find(Hotel.class, id);
        if (hotel != null) {
            entityManager.remove(hotel);
        }

        List<Room> rooms = roomRepository.findByHotelId(id);
        if (hotel != null) {
            // Удалить все комнаты отеля и их фотографии
            for (Room room : rooms) {
                room.getPhotos().clear();
                roomRepository.delete(room);
            }
        }

        if (hotel != null) {
            // Удалить все фотографии отеля
            hotel.getPhotos().clear();
            // Удалить отель
            hotelRepository.delete(hotel);
        }

        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
        }


         String baseDirectory = "src/main/resources/static/uploads/";
    Path hotelFolder = Paths.get(baseDirectory, "hotel-" + id);

    try {
        if (Files.exists(hotelFolder)) {
            // Удаление папки и всех вложенных файлов/папок
            Files.walk(hotelFolder)
                .sorted(Comparator.reverseOrder()) // сначала файлы, потом папки
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
        return "redirect:/hotels/list";
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










    
    @GetMapping("/new")
    public String newHotel(Model model) {

        model.addAttribute("hotel", new Hotel());

        return "Hotel/newHotel";
    }

    @PostMapping("/new")
    public String createHotel(HttpSession session, @ModelAttribute Hotel hotel,
            @RequestParam("photos22") List<MultipartFile> photos) throws IOException {
        if (!photos.isEmpty()) {
            // Обработка фотографий (сохранение на сервере или в базе данных)
            hotelRepository.save(hotel); // Сначала сохраняем отель
            savePhotos(hotel, photos);
        } else {
            // Обработка случая, когда нет загруженных фотографий
            // Здесь можете добавить дополнительную логику по вашему усмотрению
        }
        session.setAttribute("hotelId", hotel.getId());

        // Редирект на страницу добавления номера в комнату с передачей id отеля в
        // параметре запроса
        // return "redirect:/rooms/add?hotelId=" + hotelId;
        // Редирект на страницу отелей
        // return "redirect:/hotels/list";

        return "redirect:/rooms/add";
    }

   @PostMapping("/search")
public String searchHotels(@RequestParam("city") String keyword, Model model) {
   /*  List<Hotel> hotels = hotelRepository.findByCityContainingIgnoreCaseOrCountryContainingIgnoreCase(keyword, keyword);
     */
     List<Hotel> hotels = hotelRepository.findByCityIgnoreCaseOrCountryIgnoreCase(keyword, keyword);
    

    model.addAttribute("hotels", hotels);

    // Получение списка услуг для каждого отеля
    Map<Long, List<Service>> hotelServicesMap = new HashMap<>();
    for (Hotel hotel : hotels) {
        List<Service> services = hotel.getServices();
        hotelServicesMap.put(hotel.getId(), services != null ? services : new ArrayList<>());
    }
    model.addAttribute("hotelServicesMap", hotelServicesMap);

    // Средняя оценка
    Map<Long, Double> avgEval = new HashMap<>();
    for (Hotel hotel : hotels) {
        Double avgEval2 = commentRepository.findAverageEvaluationByHotelId(hotel.getId());
        avgEval.put(hotel.getId(), avgEval2 != null ? avgEval2 : 0.0);
    }
    model.addAttribute("averageEvaluation", avgEval);

    // Services for rooms
    List<List<ThingsRoom>> thingsRoom = new ArrayList<>();
    for (Hotel hotel : hotels) {
        List<ThingsRoom> list = getRoomsAndServicesByHotelId(hotel.getId());
        thingsRoom.add(list);
    }
    model.addAttribute("services", thingsRoom);

    // Cheapest room
    List<Room> cheapestRooms = new ArrayList<>();
    for (Hotel hotel : hotels) {
        List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
        rooms.stream().min(Comparator.comparingDouble(Room::getPrice)).ifPresent(cheapestRooms::add);
    }
    model.addAttribute("rooms", cheapestRooms);

    return "Hotel/hotelsList";
}

    
    private void savePhotos(Hotel hotel, List<MultipartFile> photos) throws IOException {
         if (photos == null || photos.isEmpty() || photos.stream().allMatch(MultipartFile::isEmpty)) {
        return; // Если нет фото, выходим
    }
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
                // String decodedFileName = URLDecoder.decode(fileName, "UTF-8");

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
    hotel.addPhoto(transliteratedText); // Добавляем имя файла в список
    hotelRepository.save(hotel);          // Сохраняем изменения
} catch (IOException e) {
    e.printStackTrace();
    throw e;  // Пробрасываем дальше, чтобы не скрывать ошибку
} catch (Exception e) {
    e.printStackTrace(); // Логируем другие ошибки, связанные с сохранением
    throw e;
}

            }
        }
    }

    private void saveExitPhotos(Hotel hotel, List<String> photoPaths) throws IOException {
        // Process existing photos
        for (String existingPhoto : photoPaths) {
            // You may want to validate the existing photo path here
            hotel.addPhoto(existingPhoto);
        }
        hotelRepository.save(hotel);
    }

   
    private void savePhotosFoEdit(Hotel hotel, List<MultipartFile> newPhotos, List<String> existingPhotos)
            throws IOException {
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
