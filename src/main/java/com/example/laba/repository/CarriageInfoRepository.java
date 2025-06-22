package com.example.laba.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.example.laba.models.CarriageInfo;

public interface CarriageInfoRepository extends CrudRepository<CarriageInfo, Long> { 
List<CarriageInfo> findByTrainTicketTicketId(Long ticketId);

}
