package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Token;
import com.pweb.backend.dao.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Integer> {
    Token findByToken(String token);

    Token findByUser(User user);

    void deleteByToken(String token);

    void deleteByUser(User user);

    boolean existsByToken(String token);

    boolean existsByUser(User user);

    default boolean isTokenExpired(String token){
        return !findByToken(token).getExpiresAt().after(new java.sql.Date(System.currentTimeMillis()));
    }
}
