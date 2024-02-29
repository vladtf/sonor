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
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());

        return userRepository.save(user);
    }

    public boolean userExists(LoginRequest loginRequest) {
        return userRepository.existsByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
    }

    public Token login(LoginRequest form) {
        if (!userRepository.existsByEmailAndPassword(form.getEmail(), form.getPassword())) {
            return null;
        }

        User user = userRepository.findByEmail(form.getEmail());

        String device = "TO REMOVE";
        Token token = new Token(device, user);

        String text = MessageFormat.format("<html><body><p>You have logged in from {0} at {1}. Your token is {2}</p><p>Follow this link to authenticate to your account: <a href=\"{3}\">Activate</a></p></body></html>",
                device, new Date(), token.getToken(), device + "/activate/" + token.getToken());

        return tokenRepository.save(token);
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<String> getRegisteredUsers() {
        return userRepository.findAll().stream().map(User::getEmail).toList();
    }

    public List<String> getAllEmails(String token) {
        List<User> all = userRepository.findAll();
        return all.stream().map(User::getEmail).toList();
    }
}
