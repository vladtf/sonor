package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MeterRegistry meterRegistry;

    public PostController(PostService postService, MeterRegistry meterRegistry) {
        this.postService = postService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all posts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of posts"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
            })
    public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/posts/all").increment();
        Pageable pageable = PageRequest.of(page, size, Post.DEFAULT_SORT);
        return new ResponseEntity<>(buildResponseBody(postService.getAllPosts(pageable)), HttpStatus.OK);
    }

    @GetMapping("/search")
    @Secured("ROLE_USER")
    @Operation(summary = "Search posts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of posts"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
            })
    public ResponseEntity<Page<PostResponse>> searchPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam String searchTerm) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/posts/search").increment();
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(buildResponseBody(postService.searchPosts(pageable, searchTerm)), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Get a post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<PostResponse> getPost(@PathVariable Integer id) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/posts/{id}").increment();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.getPost(user, id);
        PostResponse postResponse = new PostResponse();
        postResponse.id = post.getId();
        postResponse.title = post.getTitle();
        postResponse.content = post.getContent();
        postResponse.category = post.getCategory();
        postResponse.author = post.getAccount().getUsername();
        postResponse.createdAt = post.getCreatedAt();

        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    @Operation(summary = "Create a post",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Post created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> createPost(@RequestBody NewPostRequest newPostRequest) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/posts/create").increment();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.createPost(user, newPostRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_USER")
    @Operation(summary = "Delete a post",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/posts/delete").increment();
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
            postResponse.author = post.getAccount().getUsername();
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
