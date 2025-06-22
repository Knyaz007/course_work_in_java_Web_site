package com.example.laba.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "Description")
    private String description;

    @Column(name = "MealPrice")
    private Double mealPrice;

    @Column(name = "RoomNumber")
    private Integer roomNumber;

    @Column(name = "Price", nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "room_id") // foreign key in ThingsRoom table
    private List<ThingsRoom> thingsRoom = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "RoomPhotos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_path")
    private List<String> photos = new ArrayList<>();

    public void addPhoto(String photoPath) {
        this.photos.add(photoPath);
    }

    public void removePhoto(String photoPath) {
        photos.removeIf(path -> path.equals(photoPath));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
