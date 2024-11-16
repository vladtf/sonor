package com.pweb.backend.services;

import com.pweb.backend.controllers.AuthenticationController;
import com.pweb.backend.dao.entities.Account;
import com.pweb.backend.dao.repositories.AccountRepository;
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

    private final AccountRepository accountRepository;
    /*private final RoleRepository roleRepository;*/
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AccountRepository accountRepository, /*RoleRepository roleRepository,*/ PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        /*this.roleRepository = roleRepository;*/
        this.passwordEncoder = passwordEncoder;
    }


    public Account registerUser(AuthenticationController.RegisterRequest registerRequest) {
        // check if username is already taken
        if (accountRepository.existsByUsername(registerRequest.username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }
        Account account = new Account();

        account.setUsername(registerRequest.username);
        /*account.setPassword(passwordEncoder.encode(registerRequest.password));*/

        account = accountRepository.save(account);

        /*Role role = new Role();
        role.setName(Role.RoleEnum.USER);
        role.setUser(account);
        roleRepository.save(role);*/

        return account;
    }

    public List<String> getAllUsernames() {
        return accountRepository.findAll().stream().map(Account::getUsername).toList();
    }

    //    @Transactional
    public void deleteUser(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        accountRepository.deleteById(id);
    }


    public Page<Account> getAllUsers(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Page<Account> searchUsers(String query, Pageable pageable) {
        return accountRepository.findAllByUsernameContaining(query, pageable);
    }
}
