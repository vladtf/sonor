
package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Feedback;
import com.pweb.backend.services.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;


    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks()
                .stream()
                .map(feedback -> {
                    FeedbackResponse response = new FeedbackResponse();
                    response.id = feedback.getId();
                    response.content = feedback.getContent();
                    response.username = feedback.getUser().getUsername();
                    response.createdAt = feedback.getCreatedAt();
                    response.satisfaction = feedback.getSatisfaction();
                    response.feature = feedback.getFeature();
                    return response;
                })
                .collect(java.util.stream.Collectors.toList()));
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

