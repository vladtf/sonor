package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(buildResponseBody(postService.getAllPosts(pageable)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<PostResponse> getPost(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.getPost(user, id);
        PostResponse postResponse = new PostResponse();
        postResponse.id = post.getId();
        postResponse.title = post.getTitle();
        postResponse.content = post.getContent();
        postResponse.category = post.getCategory();
        postResponse.author = post.getUser().getUsername();
        postResponse.createdAt = post.getCreatedAt();

        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> createPost(@RequestBody NewPostRequest newPostRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Post> posts = postService.createPost(user, newPostRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.deletePost(user, deletePostRequest.id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Page<PostResponse> buildResponseBody(Page<Post> posts) {
        return posts.map(post -> {
            PostResponse postResponse = new PostResponse();
            postResponse.id = post.getId();
            postResponse.title = post.getTitle();
            postResponse.content = post.getContent();
            postResponse.category = post.getCategory();
            postResponse.author = post.getUser().getUsername();
            postResponse.createdAt = post.getCreatedAt();

            return postResponse;
        });
    }


    public static class NewPostRequest {
        public String title;
        public String content;
        public Post.PostCategory category;
    }

    public static class DeletePostRequest {
        public Integer id;
    }

    public static class PostResponse {
        public Integer id;
        public String title;
        public String content;
        public String author;
        public Date createdAt;
        public Post.PostCategory category;
    }


}
