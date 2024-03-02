package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.services.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @RequestMapping("/all")
    @Secured("ROLE_USER")
    public ResponseEntity<Collection<ConversationResponse>> getAllConversations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(conversationService.getAllConversations(user.getUsername())
                .stream()
                .map(conversation -> {
                    ConversationResponse response = new ConversationResponse();
                    response.id = conversation.getId();
                    response.name = conversation.getName();
                    response.participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
                    return response;
                })
                .collect(Collectors.toList()));
    }

    @RequestMapping("/create")
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

    @RequestMapping("/delete")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deleteConversation(Integer id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/addUser")
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

    @RequestMapping("/removeUser")
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

    @RequestMapping("/addMessage")
    @Secured("ROLE_USER")
    public ResponseEntity<ConversationResponse> addMessageToConversation(@RequestBody AddMessageRequest request) {
        Conversation conversation = conversationService.addMessageToConversation(request);
        return ResponseEntity.ok(new ConversationResponse() {
            {
                id = conversation.getId();
                name = conversation.getName();
                participants = conversation.getUsers().stream().map(com.pweb.backend.dao.entities.User::getUsername).collect(Collectors.toList());
            }
        });
    }

    public static class ConversationResponse {
        public Integer id;
        public String name;
        public Collection<String> participants;
    }

    public static class CreateConversationRequest {
        public String name;
    }

    public static class AddMessageRequest {
        public Integer conversationId;
        public String content;
        public String username;
    }

    public static class ChangeUserConversationRequest {
        public Integer conversationId;
        public String username;
    }
}
