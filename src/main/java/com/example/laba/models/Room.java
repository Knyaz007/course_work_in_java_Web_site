package com.example.laba.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "MealPrice", nullable = false)
    private Double mealPrice;

    @Column(name = "RoomNumber", nullable = false)
    private Integer roomNumber;

    @Column(name = "Price", nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = true)
    private Hotel hotel;

    @ElementCollection
    @CollectionTable(name = "RoomPhotos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_path", nullable = true)
    private List<String> photos = new ArrayList<>();
    // Другие характеристики номера, например, тип номера, количество кроватей и т.д.

    public void addPhoto(String photoPath) {
        this.photos.add(photoPath);
    }

    public void removePhoto(String photoPath) {
        Iterator<String> iterator = this.photos.iterator();

        while (iterator.hasNext()) {
            String path = iterator.next();
            if (path.equals(photoPath)) {
                iterator.remove();
                break; // Exit the loop once the photo is removed
            }
        }


    }
    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    // Дополнительные методы и характеристики номера

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    public Double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(Double mealPrice) {
        this.mealPrice = mealPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
