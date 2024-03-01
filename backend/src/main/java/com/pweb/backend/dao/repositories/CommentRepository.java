package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
    Collection<Comment> findAllByPostId(Integer postId);

    Collection<Comment> findAllByUserUsername(String username);
}
