package com.pweb.backend.services;

import com.pweb.backend.controllers.PostController;
import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.PostRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAllPosts(org.springframework.security.core.userdetails.User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        return postRepository.findAllByUser(userOptional.get());
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public void createPost(org.springframework.security.core.userdetails.User user, PostController.NewPostRequest newPostRequest) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        try {
            User user1 = userOptional.get();
            Post post = new Post();
            post.setTitle(newPostRequest.title);
            post.setContent(newPostRequest.content);
            post.setCategory(newPostRequest.category);
            post.setUser(user1);
            postRepository.save(post);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request.");
        }
    }

    @Transactional
    public void deletePost(org.springframework.security.core.userdetails.User user, Integer id) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");
        }

        Post post = postOptional.get();

        // check if the post is owned by the user
        if (!post.getUser().getId().equals(userOptional.get().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this post.");
        }

        try {
            postRepository.deleteByIdAndUser(id, userOptional.get());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request.");
        }
    }

    public Post getPost(org.springframework.security.core.userdetails.User user, Integer id) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");
        }
        return postOptional.get();
    }

    public Page<Post> searchPosts(Pageable pageable, String searchTerm) {
        return postRepository.findAllByTitleContainingOrContentContaining(searchTerm, searchTerm, pageable);
    }
}
