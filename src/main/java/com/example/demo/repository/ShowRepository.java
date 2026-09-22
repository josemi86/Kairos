package com.example.demo.repository;

import com.example.demo.model.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends MongoRepository<Show, String> {
    Show findById(Long id);
}