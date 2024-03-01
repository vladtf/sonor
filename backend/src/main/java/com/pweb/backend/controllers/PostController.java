package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(postService.getAllPosts(user).stream().map(post -> {
            PostResponse postResponse = new PostResponse();
            postResponse.id = post.getId();
            postResponse.title = post.getTitle();
            postResponse.content = post.getContent();
            postResponse.category = post.getCategory();
            return postResponse;
        }).toList(), HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    public ResponseEntity<List<PostResponse>> createPost(@RequestBody NewPostRequest newPostRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Post> posts = postService.createPost(user, newPostRequest);
        return new ResponseEntity<>(posts.stream().map(post -> {
            PostResponse postResponse = new PostResponse();
            postResponse.id = post.getId();
            postResponse.title = post.getTitle();
            postResponse.content = post.getContent();
            postResponse.category = post.getCategory();
            return postResponse;
        }).toList(), HttpStatus.CREATED);
    }


    public static class NewPostRequest {
        public String title;
        public String content;
        public Post.PostCategory category;
    }

    public static class PostResponse {
        public Integer id;
        public String title;
        public String content;
        public Post.PostCategory category;
    }


}
