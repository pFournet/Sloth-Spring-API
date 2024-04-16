package com.example.demo.controller;

import com.example.demo.model.ProblemSolution;
import com.example.demo.repository.ProblemSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProblemSolutionController {

    @Autowired
    private ProblemSolutionRepository repository;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/problems")
    public ResponseEntity<List<String>> getResponses(@RequestBody Map<String, String> requestBody) {
        // Utiliser RestTemplate pour envoyer une requête POST à l'API Python
        String url = pythonApiUrl + "/predict"; // s'assurer que c'est l'URL correcte
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

        // Extraire les prédictions de la réponse
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.getBody().get("predictions");

        // Extraire et retourner uniquement les labels des prédictions
        List<String> responses = predictions.stream()
                .map(prediction -> prediction.get("label").toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/solutions")
    public ResponseEntity<List<ProblemSolution>> saveSolutions(@RequestBody List<ProblemSolution> problemSolutions) {
        List<ProblemSolution> savedSolutions = problemSolutions.stream()
                .map(solution -> repository.save(solution))
                .collect(Collectors.toList());
        return ResponseEntity.ok(savedSolutions);
    }

}
