package com.example.laba.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightImportUtil {

    // Форматы для преобразования даты и времени
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Импортирует рейсы из CSV-файла.
     * @param filePath Путь к CSV-файлу
     * @return Список объектов Flight
     */
    public static List<Flight> importFlightsFromCSV(String filePath) {
        List<Flight> flights = new ArrayList<>();

        // Чтение данных из файла с использованием BufferedReader
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;  // Флаг для пропуска первой строки (заголовка)

            // Чтение строк файла
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Пропускаем заголовок
                    continue;
                }

                // Разделяем строку по запятым
                String[] values = line.split(",");

                // Проверка на достаточность данных в строке
                if (values.length < 12) {
                    System.err.println("Строка пропущена (недостаточно данных): " + line);
                    continue;
                }

                // Создание объекта рейса
                Flight flight = new Flight();
                // flightId пропускается, так как это автоинкрементируемое поле в базе данных

                // Присваиваем значения из CSV
                flight.setFlightNumber(values[1].trim());  // Номер рейса
                flight.setDeparture(values[2].trim());  // Место отправления
                flight.setDepartureAirport(values[3].trim());  // Аэропорт отправления
                flight.setDestination(values[4].trim());  // Место назначения
                flight.setArrivalAirport(values[5].trim());  // Аэропорт прибытия
                flight.setDepartureDate(parseDate(values[6].trim())); 
               /*  flight.setDepartureDate(LocalDateTime.parse(values[6].trim(), formatter));
 */
                flight.setDepartureTime(parseTime(values[7].trim()));

                flight.setArrivalDate(parseDate(values[8].trim()));

                flight.setArrivalTime(parseTime(values[9].trim()));

            /*      flight.setDepartureTime(parseTime(values[7].trim()));  // Время отправления
                flight.setArrivalDate(parseDate(values[8].trim()));  // Дата прибытия
                flight.setArrivalTime(parseTime(values[9].trim()));  // Время  прибытия */
                flight.setAvailableSeats(Integer.parseInt(values[10].trim()));  // Доступные места
                flight.setPrice(Double.parseDouble(values[11].trim()));  // Цена билета
                flight.setFlightTime(Double.parseDouble(values[12].trim()));  // Время полета
                flight.setWifiAvailability(Boolean.parseBoolean(values[13].trim()));  // Наличие Wi-Fi

                // Присваиваем дополнительные поля, если они есть
                flight.setAvailablePaymentMethods(values.length > 14 ? values[14].trim() : "");
                flight.setPetPolicy(values.length > 15 ? values[15].trim() : "");

                // Добавляем рейс в список
                flights.add(flight);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении CSV: " + e.getMessage());
        }

        return flights;
    }

    /**
     * Метод для безопасного парсинга строки в LocalDate.
     * @param dateStr Строка с датой
     * @return LocalDate или null в случае ошибки
     */
    /* private static LocalDateTime parseDate(String dateStr) {
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                // Парсим только дату с помощью LocalDate
                LocalDate date = LocalDate.parse(dateStr);
                // Создаем LocalDateTime с текущим временем или с заданным временем
                return date.atTime(LocalTime.MIDNIGHT);  // Пример: если время не указано, устанавливаем 00:00
            }
        } catch (Exception e) {
            System.err.println("Ошибка при разборе даты: " + dateStr);
        }
        return null;  // Возвращаем null, если не удалось распарсить
    } */
    

    /**
     * Метод для безопасного парсинга строки в LocalTime.
     * @param timeStr Строка с временем
     * @return LocalTime или null в случае ошибки
     */
   /*  private static LocalDateTime parseTime(String timeStr) {
        try {
            if (timeStr != null && !timeStr.isEmpty()) {
                return LocalDateTime.parse(timeStr, timeFormatter);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при разборе времени: " + timeStr);
        }
        return null;  // Возвращаем null, если не удалось распарсить
    } */
    /* private static LocalDateTime parseTime(String timeStr) {
        try {
            if (timeStr != null && !timeStr.isEmpty()) {
                // Парсим только время с помощью LocalTime
                LocalTime time = LocalTime.parse(timeStr);
                // Создаем LocalDateTime с текущей датой и найденным временем
                return time.atDate(LocalDate.now());
            }
        } catch (Exception e) {
            System.err.println("Ошибка при разборе времени: " + timeStr);
        }
        return null;  // Возвращаем null, если не удалось распарсить
    }
 */
// Метод для парсинга даты
private static LocalDate parseDate(String dateStr) {
    try {
        if (dateStr != null && !dateStr.isEmpty()) {
            // Используем стандартный формат для даты "yyyy-MM-dd"
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            System.out.println(date);  // Печать для отладки
            return date;  // Возвращаем только дату
        }
    } catch (Exception e) {
        System.err.println("Ошибка при разборе даты: " + dateStr);
    }
    return null;  // Возвращаем null, если не удалось распарсить
}

// Метод для парсинга времени
private static LocalTime parseTime(String timeStr) {
    try {
        if (timeStr != null && !timeStr.isEmpty()) {
            // Используем формат "HH:mm" для парсинга времени
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeStr, timeFormatter);
            System.out.println(time);  // Печать для отладки
            return time;  // Возвращаем только время
        }
    } catch (Exception e) {
        System.err.println("Ошибка при разборе времени: " + timeStr);
    }
    return null;  // Возвращаем null, если не удалось распарсить
}


    
    
}
