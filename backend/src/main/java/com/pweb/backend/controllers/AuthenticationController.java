package com.pweb.backend.controllers;

import com.pweb.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final com.pweb.backend.services.UserService userService;

    public AuthenticationController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, com.pweb.backend.services.UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
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
    public ResponseEntity<com.pweb.backend.dao.entities.User> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.username == null || registerRequest.username.isEmpty()) {
            throw new RuntimeException("username not provided");
        }

        return ResponseEntity.ok(userService.registerUser(registerRequest));
    }

    @GetMapping("/logout")
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
