package com.pweb.backend.services;

import com.pweb.backend.controllers.CommentController;
import com.pweb.backend.dao.entities.Comment;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.CommentRepository;
import com.pweb.backend.dao.repositories.PostRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Collection<Comment> getAllComments(Integer postId) {
        // check if post exists
        // if not, throw exception
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post with id " + postId + " does not exist");
        }

        return commentRepository.findAllByPostId(postId);
    }

    public void addComment(org.springframework.security.core.userdetails.User user, CommentController.AddCommentRequest addCommentRequest) {
        // check if post exists
        // if not, throw exception
        if (!postRepository.existsById(addCommentRequest.postId)) {
            throw new IllegalArgumentException("Post with id " + addCommentRequest.postId + " does not exist");
        }

        Optional<User> found = userRepository.findByUsername(user.getUsername());

        if (found.isEmpty()) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " does not exist");
        }

        Comment comment = new Comment(addCommentRequest.content, found.get(), postRepository.findById(addCommentRequest.postId).get());
        commentRepository.save(comment);
    }

    public void deleteComment(org.springframework.security.core.userdetails.User user, Integer id) {
        // check if comment exists
        // if not, throw exception
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment with id " + id + " does not exist");
        }

        Optional<User> found = userRepository.findByUsername(user.getUsername());

        if (found.isEmpty()) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " does not exist");
        }

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            throw new IllegalArgumentException("Comment with id " + id + " does not exist");
        }

        if (!comment.get().getUser().equals(found.get())) {
            throw new IllegalArgumentException("You are not the author of this comment");
        }

        commentRepository.deleteById(id);
    }
}
