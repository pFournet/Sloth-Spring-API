package com.example.demo.controller;

import com.example.demo.model.ProblemSolution;
import com.example.demo.repository.ProblemSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    public ResponseEntity<List<String>> getResponses(@RequestBody String problem) {
        String url = pythonApiUrl + "/solve";
        List<String> responses = Arrays.asList(restTemplate.postForObject(url, problem, String[].class));
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/solutions")
    public ResponseEntity<ProblemSolution> saveSolution(@RequestBody ProblemSolution problemSolution) {
        ProblemSolution saved = repository.save(problemSolution);
        return ResponseEntity.ok(saved);
    }
}
