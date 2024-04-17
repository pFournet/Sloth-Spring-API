package com.example.demo.controller;

import com.example.demo.model.ProblemSolution;
import com.example.demo.repository.ProblemSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ProblemSolutionController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolutionController.class);

    @Autowired
    private ProblemSolutionRepository repository;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/problems")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> getResponses(@RequestBody Map<String, String> requestBody) {
        String url = pythonApiUrl + "/predict";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
            if (response.getBody() == null) {
                return ResponseEntity.badRequest().body("Response body is null");
            }
            List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.getBody().get("predictions");
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            logger.error("Error while calling Python API: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to call Python API");
        }
    }

    @PostMapping("/solutions")
    public ResponseEntity<List<ProblemSolution>> saveSolutions(@RequestBody List<ProblemSolution> problemSolutions) {
        List<ProblemSolution> savedSolutions = problemSolutions.stream()
                .map(solution -> repository.save(solution))
                .collect(Collectors.toList());

        return ResponseEntity.ok(savedSolutions);
    }

    @PostMapping("/submit-problem-solution")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<ProblemSolution> submitProblemSolution(@RequestBody ProblemSolution problemSolution) {
        logger.info("Received problem solution for saving: {}", problemSolution);
        try {
            ProblemSolution savedSolution = repository.save(problemSolution);
            return ResponseEntity.ok(savedSolution);
        } catch (Exception e) {
            logger.error("Error saving problem solution: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
