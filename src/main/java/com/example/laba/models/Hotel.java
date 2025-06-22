package com.example.laba.models;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Сountry", nullable = false)
    private String country;

    @Column(name = "Type", nullable = false)
    private String type;

    @Column(name = "Region", nullable = false)
    private String region;

    @Column(name = "City", nullable = false)
    private String city;

    @Column(name = "Street", nullable = false)
    private String street;

    @Column(name = "House", nullable = false)
    private String house;

    @Column(name = "AvailableRooms")
    private Integer availableRooms;

    // Список комментариев
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments = new ArrayList<>();

    // Список фотографий
    @ElementCollection
    @CollectionTable(name = "HotelPhotos", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "photo_path", nullable = true)
    private List<String> photos = new ArrayList<>();

    // Список предоставляемых услуг
    @ManyToMany
    @JoinTable(
        name = "hotel_service",
        joinColumns = @JoinColumn(name = "hotel_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    // Удобный метод для добавления фотографии
    public void addPhoto(String photoPath) {
        this.photos.add(photoPath);
    }

    // Удаление фотографии по пути
    public void removePhoto(String photoPath) {
        Iterator<String> iterator = this.photos.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (path.equals(photoPath)) {
                iterator.remove();
                break;
            }
        }
    }

    // Добавление сервиса
    public void addService(Service service) {
        this.services.add(service);
    }

    // Подсчёт средней оценки по комментариям
    public Double getAverageCommentEvaluation() {
        if (comments == null || comments.isEmpty()) {
            return 0.0;
        }
        int sum = comments.stream().mapToInt(Comment::getEvaluation).sum();
        return (double) sum / comments.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
