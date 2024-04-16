package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class IndexController {

    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public IndexController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(path = "/")
    @Secured({"client", "admin"}) // Replace with the appropriate role
    public ResponseEntity<String> sendPhraseToPythonApi(@RequestBody Map<String, String> requestBody) {
        // Get the current authenticated user
        OAuth2User user = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user has ROLE_ADMIN authority
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"))) {
            // Send POST request to the external Python API with the phrase provided by the user
            ResponseEntity<String> response = restTemplate.postForEntity(pythonApiUrl + "/predict", requestBody, String.class);
            return ResponseEntity.ok(response.getBody());
        } else {
            // Return forbidden status if the user does not have admin authority
            return ResponseEntity.status(403).body("User does not have permission to perform this action.");
        }
    }

    @GetMapping(path = "/unauthenticated")
    public Map<String, String> unauthenticatedRequests() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is an unauthenticated endpoint.");
        return response;
    }
}
