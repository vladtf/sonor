package com.pweb.backend.controllers;

import com.pweb.backend.requests.LoginRequest;
import com.pweb.backend.requests.RegisterRequest;
import com.pweb.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

            List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            return ResponseEntity.ok(token);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/register")
    public com.pweb.backend.dao.entities.User registerUser(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            throw new RuntimeException("username not provided");
        }

        return userService.registerUser(registerRequest);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {

        return ResponseEntity.ok().body("Logged out");
    }

    public static class LoginResponse {
        private final String token;
        private final List<String> roles;

        public LoginResponse(String token, List<String> roles) {
            this.token = token;
            this.roles = roles;
        }

        public String getToken() {
            return token;
        }

        public List<String> getRoles() {
            return roles;
        }
    }

}
