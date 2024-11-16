package com.pweb.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;

    @Value("${authentication.server.url}")
    private String authenticationServerUrl;

    public AuthenticationController(RestTemplate restTemplate, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        meterRegistry.counter("api_requests_total", "endpoint", "/login").increment();
        logger.info("Received login request for user: " + request.username);
        String url = authenticationServerUrl + "/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        try {
            logger.info("Sending login request to authentication server");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            String token = (String) response.getBody().get("token");
            return ResponseEntity.ok(token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token);
        } catch (Exception ex) {
            logger.error("Login request failed for user: " + request.username, ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        meterRegistry.counter("api_requests_total", "endpoint", "/register").increment();
        logger.info("Received register request for user: " + registerRequest.username);
        String url = authenticationServerUrl + "/register";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);
        try {
            logger.info("Sending register request to authentication server");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception ex) {
            logger.error("Register request failed for user: " + registerRequest.username, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        }
    }

    @GetMapping("/logout")
    @Operation(summary = "Logout",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully")
            })
    public ResponseEntity<String> logout() {
        meterRegistry.counter("api_requests_total", "endpoint", "/logout").increment();
        return ResponseEntity.ok().body("Logged out");
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
    }
}
