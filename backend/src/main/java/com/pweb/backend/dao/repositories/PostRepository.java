package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Account;
import com.pweb.backend.dao.entities.Post;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findAllByAccount(Account account);

    @NotNull List<Post> findAll();

    void deleteByIdAndAccount(Integer id, Account account);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByTitleContainingOrContentContaining(String searchTerm, String searchTerm1, Pageable pageable);
}
