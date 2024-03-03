package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.dao.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findAllByUser(User user);

    @NotNull List<Post> findAll();

    void deleteByIdAndUser(Integer id, User user);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByTitleContainingOrContentContaining(String searchTerm, String searchTerm1, Pageable pageable);
}
