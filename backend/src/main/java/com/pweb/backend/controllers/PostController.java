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
        return new ResponseEntity<>(buildResponseBody(postService.getAllPosts(user)), HttpStatus.OK);
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
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    public ResponseEntity<List<PostResponse>> createPost(@RequestBody NewPostRequest newPostRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Post> posts = postService.createPost(user, newPostRequest);
        return new ResponseEntity<>(buildResponseBody(posts), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_USER")
    public ResponseEntity<List<PostResponse>> deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.deletePost(user, deletePostRequest.id);
        return new ResponseEntity<>(buildResponseBody(postService.getAllPosts(user)), HttpStatus.OK);
    }

    private List<PostResponse> buildResponseBody(List<Post> posts) {
        return posts.stream().map(post -> {
            PostResponse postResponse = new PostResponse();
            postResponse.id = post.getId();
            postResponse.title = post.getTitle();
            postResponse.content = post.getContent();
            postResponse.category = post.getCategory();
            return postResponse;
        }).toList();
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
        public Post.PostCategory category;
    }


}
