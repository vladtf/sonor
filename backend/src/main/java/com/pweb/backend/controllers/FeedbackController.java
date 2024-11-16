package com.pweb.backend.controllers;

import com.pweb.backend.services.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MeterRegistry meterRegistry;


    public FeedbackController(FeedbackService feedbackService, MeterRegistry meterRegistry) {
        this.feedbackService = feedbackService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all feedbacks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of feedbacks"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
            })
    public ResponseEntity<Page<FeedbackResponse>> getAllFeedbacks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/feedbacks/all").increment();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable).map(feedback -> new FeedbackResponse() {
            {
                id = feedback.getId();
                content = feedback.getContent();
                username = feedback.getAccount().getUsername();
                satisfaction = feedback.getSatisfaction();
                feature = feedback.getFeature();
                createdAt = feedback.getCreatedAt();
            }
        }));
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    @Operation(summary = "Create a feedback",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Feedback created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> createFeedback(@RequestBody CreateFeedbackRequest request) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/feedbacks/create").increment();
        User user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        feedbackService.createFeedback(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Delete a feedback",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Feedback deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/feedbacks/delete/{id}").increment();
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

