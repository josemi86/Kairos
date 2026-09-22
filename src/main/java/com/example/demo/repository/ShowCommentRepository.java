package com.example.demo.repository;

import com.example.demo.model.ShowComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowCommentRepository extends MongoRepository<ShowComment, String> {
    List<ShowComment> findByShowId(Long showId);
}
