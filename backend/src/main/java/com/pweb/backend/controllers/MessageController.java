package com.pweb.backend.controllers;

import com.pweb.backend.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Collection<MessageResponse>> getAllMyMessages() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(messageService.getRecentMessages(user.getUsername())
                .stream()
                .map(message -> new MessageResponse() {
                    {
                        id = message.getId();
                        content = message.getContent();
                        author = message.getUser().getUsername();
                        createdAt = message.getCreatedAt();
                        conversationName = message.getConversation().getName();
                        conversationId = message.getConversation().getId();
                    }
                })
                .collect(Collectors.toList()));
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
