package com.pweb.backend.services;

import com.pweb.backend.dao.entities.Role;
import com.pweb.backend.dao.repositories.RoleRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseUserDetailsService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> new User(u.getUsername(), u.getPassword(), getAuthorities(u)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(com.pweb.backend.dao.entities.User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        // delete admin user if exists
        userRepository.findByUsername("admin").ifPresent(userRepository::delete);

        com.pweb.backend.dao.entities.User user = new com.pweb.backend.dao.entities.User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        user = userRepository.findByUsername("admin").orElseThrow();
        roleRepository.save(new Role(Role.RoleEnum.USER, user));


        // delete user user if exists
        userRepository.findByUsername("user").ifPresent(userRepository::delete);

        com.pweb.backend.dao.entities.User user2 = new com.pweb.backend.dao.entities.User();
        user2.setUsername("user");
        user2.setPassword(passwordEncoder.encode("user"));
        userRepository.save(user2);

        user2 = userRepository.findByUsername("user").orElseThrow();
        roleRepository.save(new Role(Role.RoleEnum.USER, user2));
    }
}
