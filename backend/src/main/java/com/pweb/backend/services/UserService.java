package com.pweb.backend.services;

import com.pweb.backend.controllers.AuthenticationController;
import com.pweb.backend.dao.entities.Account;
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
    /*private final RoleRepository roleRepository;*/
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, /*RoleRepository roleRepository,*/ PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        /*this.roleRepository = roleRepository;*/
        this.passwordEncoder = passwordEncoder;
    }


    public Account registerUser(AuthenticationController.RegisterRequest registerRequest) {
        // check if username is already taken
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }
        Account account = new Account();

        account.setUsername(registerRequest.username);
        /*account.setPassword(passwordEncoder.encode(registerRequest.password));*/

        account = userRepository.save(account);

        /*Role role = new Role();
        role.setName(Role.RoleEnum.USER);
        role.setUser(account);
        roleRepository.save(role);*/

        return account;
    }

    public List<String> getAllUsernames() {
        return userRepository.findAll().stream().map(Account::getUsername).toList();
    }

    //    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }


    public Page<Account> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<Account> searchUsers(String query, Pageable pageable) {
        return userRepository.findAllByUsernameContaining(query, pageable);
    }
}
