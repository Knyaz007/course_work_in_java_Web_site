package com.example.laba.repository;

import com.example.laba.models.Employee;
import com.example.laba.models.Employee2;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository2 extends CrudRepository<Employee2, Long> {
       @Query("SELECT e.photo FROM Employee e WHERE e.employeeId = :employeeId")
    Optional<byte[]> findPhotoById(@Param("employeeId") Long employeeId);



}