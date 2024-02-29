package com.pweb.backend.config;

import com.pweb.backend.services.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationManagerConfig {

    private final PasswordEncoder passwordEncoder;
    private final DatabaseUserDetailsService databaseUserDetailsService;


    public AuthenticationManagerConfig(PasswordEncoder passwordEncoder, DatabaseUserDetailsService databaseUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.databaseUserDetailsService = databaseUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(databaseUserDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}
