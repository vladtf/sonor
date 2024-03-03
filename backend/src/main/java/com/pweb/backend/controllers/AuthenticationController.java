package com.pweb.backend.controllers;

import com.pweb.backend.security.JwtUtil;
import com.pweb.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            })
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username, request.password
                            )
                    );

            User user = (User) authenticate.getPrincipal();
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(token);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Username already taken"),
                    @ApiResponse(responseCode = "400", description = "Username cannot be empty")
            })
    public ResponseEntity<com.pweb.backend.dao.entities.User> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.username == null || registerRequest.username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
        }

        return ResponseEntity.ok(userService.registerUser(registerRequest));
    }

    @GetMapping("/logout")
    @Operation(summary = "Logout",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully")
            })
    public ResponseEntity<String> logout() {
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
