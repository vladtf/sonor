
package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Feedback;
import com.pweb.backend.services.FeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;


    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<FeedbackResponse>> getAllFeedbacks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable).map(feedback -> new FeedbackResponse() {
            {
                id = feedback.getId();
                content = feedback.getContent();
                username = feedback.getUser().getUsername();
                satisfaction = feedback.getSatisfaction();
                feature = feedback.getFeature();
                createdAt = feedback.getCreatedAt();
            }
        }));
    }

    @PostMapping("/create")
    public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody CreateFeedbackRequest request) {
        User user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Feedback feedback = feedbackService.createFeedback(request, user.getUsername());
        return ResponseEntity.ok(new FeedbackResponse() {
            {
                id = feedback.getId();
                content = feedback.getContent();
                username = feedback.getUser().getUsername();
                satisfaction = feedback.getSatisfaction();
                feature = feedback.getFeature();
                createdAt = feedback.getCreatedAt();
            }
        });
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        User user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        feedbackService.deleteFeedback(id, user.getUsername());
        return ResponseEntity.ok().build();
    }


    public static class FeedbackResponse {
        public Integer id;
        public String content;
        public String username;
        public String satisfaction;
        public String feature;
        public Date createdAt;
    }

    public static class CreateFeedbackRequest {
        public String content;
        public String satisfaction;
        public String feature;
    }
}

