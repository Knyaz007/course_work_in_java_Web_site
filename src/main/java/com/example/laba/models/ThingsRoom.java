package com.example.laba.models;


import jakarta.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "Thing")
public class ThingsRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "things_id")
    private Long id;

    @Column(name = "Category", nullable = false)
    private String category;


    @Column(name = "Title", nullable = false)
    private String title;

//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = true)
//    private Room room;

    @Column(name = "Price", nullable = true )
    private Long price;


    public Long getId() {
        return id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public Room getRoom() {
//        return room;
//    }
//
//    public void setRoom(Room room) {
//        this.room = room;
//    }
}
