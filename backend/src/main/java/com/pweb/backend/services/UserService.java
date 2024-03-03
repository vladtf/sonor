package com.pweb.backend.services;

import com.pweb.backend.controllers.AuthenticationController;
import com.pweb.backend.dao.entities.Role;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.RoleRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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


    public User registerUser(AuthenticationController.RegisterRequest registerRequest) {
        // check if username is already taken
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }
        User user = new User();

        user.setUsername(registerRequest.username);
        user.setPassword(passwordEncoder.encode(registerRequest.password));

        user = userRepository.save(user);

        Role role = new Role();
        role.setName(Role.RoleEnum.USER);
        role.setUser(user);
        roleRepository.save(role);

        return user;
    }

    public List<String> getAllUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    //    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.findAllByUsernameContaining(query, pageable);
    }
}
