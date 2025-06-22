package com.example.laba.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeId")
    private Long employeeId;

    @Column(name = "Name", length = 100)
    private String name;

    @Column(name = "LastName", length = 100)
    private String lastName;

    @Column(name = "Position", length = 100)
    private String position;

    @Column(name = "Salary")
    private Integer salary;

    @Column(name = "Hire_Date")
    private LocalDate hireDate;

    @Column(name = "Email", nullable = false, length = 255)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "Phone", nullable = false, length = 50)
    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @Lob
    @Column(name = "Photo", columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
}
