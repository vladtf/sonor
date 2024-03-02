package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.services.ConversationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    public ResponseEntity<Page<ConversationResponse>> getAllConversations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(conversationService.getAllConversations(user.getUsername(), pageable)
                .map(conversation -> new ConversationResponse() {
                    {
                        id = conversation.getId();
                        name = conversation.getName();
                        participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
                        messages = conversation.getMessages().stream().map(message -> new ConversationResponse.MessageResponse() {
                            {
                                id = message.getId();
                                content = message.getContent();
                                author = message.getUser().getUsername();
                                createdAt = message.getCreatedAt();
                            }
                        }).collect(Collectors.toList());
                    }
                }));
    }

    @GetMapping("/search")
    @Secured("ROLE_USER")
    public ResponseEntity<Page<ConversationResponse>> searchConversations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam String searchTerm) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(conversationService.searchConversations(user.getUsername(), pageable, searchTerm)
                .map(conversation -> new ConversationResponse() {
                    {
                        id = conversation.getId();
                        name = conversation.getName();
                        participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
                        messages = conversation.getMessages().stream().map(message -> new ConversationResponse.MessageResponse() {
                            {
                                id = message.getId();
                                content = message.getContent();
                                author = message.getUser().getUsername();
                                createdAt = message.getCreatedAt();
                            }
                        }).collect(Collectors.toList());
                    }
                }));
    }

    @PostMapping("/create")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> createConversation(@RequestBody CreateConversationRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Conversation conversation = conversationService.createConversation(user.getUsername(), request.name);
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
            }
        });
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deleteConversation(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        conversationService.deleteConversation(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addUser")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> addUserToConversation(@RequestBody ChangeUserConversationRequest request) {
        Conversation conversation = conversationService.addUserToConversation(request.conversationId, request.username);
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
            }
        });
    }

    @PostMapping("/removeUser")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> removeUserFromConversation(@RequestBody ChangeUserConversationRequest request) {
        Conversation conversation = conversationService.removeUserFromConversation(request.conversationId, request.username);
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
            }
        });
    }

    @PostMapping("/addMessage")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> addMessageToConversation(@RequestBody AddMessageRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Conversation conversation = conversationService.addMessageToConversation(request, user.getUsername());
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
            }
        });
    }

    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> getConversation(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Conversation conversation = conversationService.getConversation(id, user.getUsername());
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
                messages = conversation.getMessages().stream().map(message -> new ConversationResponse.MessageResponse() {
                    {
                        id = message.getId();
                        content = message.getContent();
                        author = message.getUser().getUsername();
                        createdAt = message.getCreatedAt();
                    }
                }).collect(Collectors.toList());
            }
        });
    }

    public static class ConversationResponse {
        public Integer id;
        public String name;
        public Collection<MessageResponse> messages;
        public Collection<String> participants;

        public static class MessageResponse {
            public Integer id;
            public String content;
            public String author;

            public Date createdAt;
        }
    }

    public static class CreateConversationRequest {
        public String name;
    }

    public static class AddMessageRequest {
        public Integer conversationId;
        public String content;
    }

    public static class ChangeUserConversationRequest {
        public Integer conversationId;
        public String username;
    }
}
