package com.example.laba.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

//import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

//    @Column(name = "Address", nullable = false)
//    private String address;

    @Column(name = "Сountry", nullable = false)
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "Region", nullable = false)
    private String region;
    @Column(name = "City", nullable = false)
    private String city;
    @Column(name = "Street", nullable = false)
    private String street;


    @Column(name = "House", nullable = false)
    private String house;

    //@Column(name = "Distance_to_the_center")
    @Column(name = "AvailableRooms")
    private Integer availableRooms;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE) //связанные комментарии также будут удалены.
    private List<Comment> comments = new ArrayList<>();
    public Double getAverageCommentEvaluation() {
        if (comments == null || comments.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (Comment comment : comments) {
            sum += comment.getEvaluation();
        }

        return (double) sum / comments.size();
    }

    @ElementCollection //    @ElementCollection не имеет каскадной операции удаления.
    @CollectionTable(name = "HotelPhotos", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "photo_path", nullable = true)
    private List<String> photos = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }
    public String getCity() {
        return city;
    }

    public void setHouse(String house) {
        this.house = house;
    }

//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
