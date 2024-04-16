package com.example.demo.controller;

import com.example.demo.model.ProblemSolution;
import com.example.demo.repository.ProblemSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.annotation.Secured;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProblemSolutionController {

    @Autowired
    private ProblemSolutionRepository repository;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/problems")
    @Secured({"client", "admin"}) // Ajustez selon les rôles exacts que vous utilisez
    public ResponseEntity<List<Map<String, Object>>> getResponses(@RequestBody Map<String, String> requestBody) {
        String url = pythonApiUrl + "/predict";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

        if (response.getBody() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.getBody().get("predictions");

        // Here, we return the entire Map, not just the label
        return ResponseEntity.ok(predictions);
    }


    @PostMapping("/solutions")
    public ResponseEntity<List<ProblemSolution>> saveSolutions(@RequestBody List<ProblemSolution> problemSolutions) {
        List<ProblemSolution> savedSolutions = problemSolutions.stream()
                .map(solution -> repository.save(solution))
                .collect(Collectors.toList());

        return ResponseEntity.ok(savedSolutions);
    }
}
