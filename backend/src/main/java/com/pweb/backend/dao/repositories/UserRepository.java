package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Override
    @NotNull
    List<User> findAll();
    Page<User> findAll(Pageable pageable);

    Page<User> findAllByUsernameContaining(String query, Pageable pageable);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
