package com.example.med_easy.exames;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExameRepository extends MongoRepository<Exame, String>{
    List<Exame> findByUserId(String userId);
}