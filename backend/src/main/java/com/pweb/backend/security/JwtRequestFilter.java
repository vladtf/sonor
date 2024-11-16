package com.pweb.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pweb.backend.controllers.AuthenticationController;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final RestTemplate restTemplate;

    @Value("${authentication.server.url}")
    private String authenticationServerUrl;

    @Autowired
    public JwtRequestFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Checking authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            String url = authenticationServerUrl + "/validateToken";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> tokenRequest = Collections.singletonMap("token", jwt);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(tokenRequest, headers);

            try {
                ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, entity, Map.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> body = responseEntity.getBody();
                    username = (String) body.get("username");
                    List<String> roles = (List<String>) body.get("roles");

                    // prepend "ROLE_" to each role if it doesn't already start with it
                    roles = roles.stream().map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).toList();

                    User user = new User(username, "", roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ex) {
                // Token validation failed
                logger.error("Token validation failed", ex);
            }
        } else {
            logger.warn("Authorization header is missing or invalid");
        }
        
        logger.info("Authorization header is valid");
        chain.doFilter(request, response);
    }
}

