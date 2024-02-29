package com.pweb.backend.services;

import com.pweb.backend.dao.entities.Token;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.TokenRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import com.pweb.backend.requests.LoginRequest;
import com.pweb.backend.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;


    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public User getUserByToken(String token) {
        Token updatedToken = tokenRepository.findByToken(token);
        return updatedToken.getUser();
    }

    public User registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setPassword(registerRequest.getPassword());
        user.setUsername(registerRequest.getEmail());

        return userRepository.save(user);
    }

    public List<String> getRegisteredUsers() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    public List<String> getAllEmails(String token) {
        List<User> all = userRepository.findAll();
        return all.stream().map(User::getUsername).toList();
    }
}
