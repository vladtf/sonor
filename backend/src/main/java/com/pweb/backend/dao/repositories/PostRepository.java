package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.dao.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findAllByUser(User user);
}
