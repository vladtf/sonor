package com.pweb.backend.services;

import com.pweb.backend.controllers.PostController;
import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.PostRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> createPost(org.springframework.security.core.userdetails.User user, PostController.NewPostRequest newPostRequest) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        User user1 = userOptional.get();
        Post post = new Post();
        post.setTitle(newPostRequest.title);
        post.setContent(newPostRequest.content);
        post.setCategory(newPostRequest.category);
        post.setUser(user1);
        postRepository.save(post);
        return getAllPosts(user);
    }

    @Transactional
    public void deletePost(org.springframework.security.core.userdetails.User user, Integer id) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new RuntimeException("Post not found.");
        }

        Post post = postOptional.get();

        // check if the post is owned by the user
        if (!post.getUser().getId().equals(userOptional.get().getId())) {
            throw new RuntimeException("Post not found.");
        }

        postRepository.deleteByIdAndUser(id, userOptional.get());
    }

    public Post getPost(org.springframework.security.core.userdetails.User user, Integer id) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new RuntimeException("Post not found.");
        }
        return postOptional.get();
    }
}
