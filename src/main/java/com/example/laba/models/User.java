package com.example.laba.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

//import javax.persistence.*;

import java.util.Objects;


import java.util.Objects;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long userId;

    @Column(name = "Name", nullable = true)
    private String name;

    @Column(name = "LastName", nullable = true)
    private String lastName;

    @Column(name = "Year", nullable = true)
    private Integer year;
   // @Column(name = "Email", nullable = true, unique = true)
    @Column(name = "Email", nullable = true)
   // @Email(message = "Email should be valid")
    //@NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "Phone", nullable = true)
   // @NotBlank(message = "Phone is mandatory")
    private String phone;

    @Column(name = "Photo")
    private String photo;

    @Column(name = "Password", nullable = true)
    private String password;

    @Column(name = "IsAdmin", nullable = true)
    private boolean isAdmin;

    @Column(name = "Roles", nullable = true)
    private String roles;  // Поле для хранения ролей, например, "ROLE_USER,ROLE_ADMIN"

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
