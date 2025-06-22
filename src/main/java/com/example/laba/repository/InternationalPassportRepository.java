package com.example.laba.repository;

 
import org.springframework.data.repository.CrudRepository;
import com.example.laba.models.InternationalPassport;

public interface InternationalPassportRepository  extends CrudRepository<InternationalPassport, Long> {
}