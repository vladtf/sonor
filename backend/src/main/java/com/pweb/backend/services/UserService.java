package com.pweb.backend.services;

import com.pweb.backend.dao.entities.Role;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.RoleRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import com.pweb.backend.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public User registerUser(RegisterRequest registerRequest) {
        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user = userRepository.save(user);

        Role role = new Role();
        role.setName(Role.RoleEnum.USER);
        role.setUser(user);
        roleRepository.save(role);

        return user;
    }

    public List<String> getRegisteredUsers() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    public List<String> getAllEmails(String token) {
        List<User> all = userRepository.findAll();
        return all.stream().map(User::getUsername).toList();
    }

    public List<String> getAllUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }
}
