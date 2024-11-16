package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Account, Integer> {
    @Override
    @NotNull
    List<Account> findAll();
    Page<Account> findAll(Pageable pageable);

    Page<Account> findAllByUsernameContaining(String query, Pageable pageable);

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);
}
