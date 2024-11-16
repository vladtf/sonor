package com.pweb.backend.controllers;

import com.pweb.backend.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final MeterRegistry meterRegistry;

    public MessageController(MessageService messageService, MeterRegistry meterRegistry) {
        this.messageService = messageService;
        this.meterRegistry = meterRegistry;
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Delete a message",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Message not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/messages/delete/{id}").increment();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageService.deleteMessage(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all my messages",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "200", description = "List of messages"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Page<MessageResponse>> getAllMyMessages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        meterRegistry.counter("api_requests_total", "endpoint", "/api/messages/mine").increment();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());

        return ResponseEntity.ok(messageService.getRecentMessages(user.getUsername(), pageable)
                .map(message -> {
                    MessageResponse response = new MessageResponse();
                    response.id = message.getId();
                    response.content = message.getContent();
                    response.author = message.getAccount().getUsername();
                    response.createdAt = message.getCreatedAt();
                    response.conversationName = message.getConversation().getName();
                    response.conversationId = message.getConversation().getId();
                    return response;
                }));
    }

    public static class MessageResponse {
        public Integer id;
        public String content;
        public String author;
        public Date createdAt;
        public String conversationName;
        public Integer conversationId;
    }

}
