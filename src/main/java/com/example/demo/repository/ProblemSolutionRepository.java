package com.example.demo.repository;

import com.example.demo.model.ProblemSolution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSolutionRepository extends MongoRepository<ProblemSolution, String> {
}
