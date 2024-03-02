package com.pweb.backend.controllers;

import com.pweb.backend.services.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageService.deleteMessage(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<MessageResponse>> getAllMyMessages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());

        return ResponseEntity.ok(messageService.getRecentMessages(user.getUsername(), pageable)
                .map(message -> {
                    MessageResponse response = new MessageResponse();
                    response.id = message.getId();
                    response.content = message.getContent();
                    response.author = message.getUser().getUsername();
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