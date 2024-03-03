package com.pweb.backend.controllers;

import com.pweb.backend.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all comments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of comments"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(commentService.getAllComments(user).stream().map(comment -> {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.id = comment.getId();
            commentResponse.content = comment.getContent();
            commentResponse.author = comment.getUser().getUsername();
            commentResponse.createdAt = comment.getCreatedAt();
            commentResponse.postId = comment.getPost().getId();
            return commentResponse;
        }).toList(), HttpStatus.OK);
    }

    @GetMapping("/post/{postId}")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all comments from a post",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of comments"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })

    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Integer postId) {
        return new ResponseEntity<>(commentService.getAllComments(postId).stream().map(comment -> {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.id = comment.getId();
            commentResponse.content = comment.getContent();
            commentResponse.author = comment.getUser().getUsername();
            commentResponse.createdAt = comment.getCreatedAt();
            commentResponse.postId = comment.getPost().getId();

            return commentResponse;
        }).toList(), HttpStatus.OK);
    }

    // endpoint to add a comment
    @PostMapping("/create")
    @Secured("ROLE_USER")
    @Operation(summary = "Add a comment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comment created"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> addComment(@RequestBody AddCommentRequest addCommentRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.addComment(user, addCommentRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // endpoint to delete a comment
    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Delete a comment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comment deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.deleteComment(user, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Update a comment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comment updated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    public ResponseEntity<Void> updateComment(@PathVariable Integer id, @RequestBody UpdateCommentRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.updateComment(user, id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public static class CommentResponse {
        public Integer id;
        public String content;
        public String author;

        public Date createdAt;

        public Integer postId;
    }

    public static class AddCommentRequest {
        public Integer postId;
        public String content;
    }

    public static class UpdateCommentRequest {
        public String content;
    }
}
