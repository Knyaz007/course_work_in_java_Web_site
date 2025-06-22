package com.example.laba.repository;

import com.example.laba.models.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT AVG(c.evaluation) FROM Comment c WHERE c.hotel.id = :hotelId")
    Double findAverageEvaluationByHotelId(Long hotelId);
}
