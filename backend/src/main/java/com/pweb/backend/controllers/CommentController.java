package com.pweb.backend.controllers;

import com.pweb.backend.services.CommentService;
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

    @GetMapping("/post/{postId}")
    @Secured("ROLE_USER")
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Integer postId) {
        return new ResponseEntity<>(commentService.getAllComments(postId).stream().map(comment -> {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.id = comment.getId();
            commentResponse.content = comment.getContent();
            commentResponse.author = comment.getUser().getUsername();
            commentResponse.createdAt = comment.getCreatedAt();
            return commentResponse;
        }).toList(), HttpStatus.OK);
    }

    // endpoint to add a comment
    @PostMapping("/create")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> addComment(@RequestBody AddCommentRequest addCommentRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.addComment(user, addCommentRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // endpoint to delete a comment
    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.deleteComment(user, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public static class CommentResponse {
        public Integer id;
        public String content;
        public String author;

        public Date createdAt;
    }

    public static class AddCommentRequest {
        public Integer postId;
        public String content;
    }
}
