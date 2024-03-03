package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.services.ConversationService;
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
    @Operation(summary = "Get all conversations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of conversations"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
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
    @Operation(summary = "Search conversations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of conversations"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
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
    @Operation(summary = "Create a new conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conversation created"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
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
    @Operation(summary = "Delete a conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conversation deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> deleteConversation(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        conversationService.deleteConversation(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addUser")
    @Secured("ROLE_USER")
    @Operation(summary = "Add a user to a conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User added to conversation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> addUserToConversation(@RequestBody ChangeUserConversationRequest request) {
        conversationService.addUserToConversation(request.conversationId, request.username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeUser")
    @Secured("ROLE_USER")
    @Operation(summary = "Remove a user from a conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User removed from conversation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> removeUserFromConversation(@RequestBody ChangeUserConversationRequest request) {
        conversationService.removeUserFromConversation(request.conversationId, request.username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addMessage")
    @Secured("ROLE_USER")
    @Operation(summary = "Add a message to a conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message added to conversation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    public ResponseEntity<Void> addMessageToConversation(@RequestBody AddMessageRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        conversationService.addMessageToConversation(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    @Operation(summary = "Get a conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conversation found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
            })
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
