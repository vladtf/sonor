package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Integer id);

    Optional<User> findByUsername(String username);

    @Override
    <S extends User> S save(S entity);

    @Override
    void delete(User entity);

    @Override
    void deleteById(Integer id);

    @Override
    boolean existsById(Integer id);

    boolean existsByUsername(String username);

}
