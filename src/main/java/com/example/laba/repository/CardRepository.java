package com.example.laba.repository;
 
import org.springframework.data.repository.CrudRepository;
import com.example.laba.models.BankCard;

public interface CardRepository extends CrudRepository<BankCard, Long> {
}