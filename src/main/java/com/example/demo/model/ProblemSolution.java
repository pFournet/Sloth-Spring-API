package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ProblemSolution {
    @Id
    private String id;
    private String problem;
    private String solution;

    // Getters et setters
}
