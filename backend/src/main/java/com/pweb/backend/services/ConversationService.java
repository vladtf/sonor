package com.pweb.backend.services;

import com.pweb.backend.controllers.ConversationController;
import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.dao.entities.Message;
import com.pweb.backend.dao.repositories.ConversationRepository;
import com.pweb.backend.dao.repositories.MessageRepository;
import com.pweb.backend.dao.repositories.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConversationService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final ConversationRepository conversationRepository;

    public ConversationService(MessageRepository messageRepository, AccountRepository accountRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.conversationRepository = conversationRepository;
    }


    public Page<Conversation> getAllConversations(String username, Pageable pageable) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
        }
        if (!accountRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return conversationRepository.findAllByAccountsUsername(username, pageable);
    }

    public Page<Conversation> searchConversations(String username, Pageable pageable, String searchTerm) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
        }

        if (!accountRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return conversationRepository.findAllByNameContainingAndAccountsUsername(searchTerm, username, pageable);
    }

    public Conversation createConversation(String username, String name) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        try {
            Conversation conversation = new Conversation();
            conversation.setName(name);
            conversation.getAccounts().add(user.get());
            conversationRepository.save(conversation);
            return conversation;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating conversation");
        }
    }

    @Transactional
    public void deleteConversation(Integer id, String username) {
        var conversation = conversationRepository.findById(id);
        if (conversation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // delete the conversation with the given id if the user with the given username is a participant in the conversation
        if (!conversation.get().getAccounts().contains(user.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a participant in this conversation");
        }

        try {
            messageRepository.deleteByConversationId(id);
            conversationRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting conversation");
        }
    }

    public void addUserToConversation(Integer conversationId, String username) {
        var conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // check if user is already in the conversation
        if (conversation.get().getAccounts().contains(user.get())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in the conversation");
        }

        try {
            conversation.get().getAccounts().add(user.get());
            conversationRepository.save(conversation.get());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error adding user to conversation");
        }
    }

    public void removeUserFromConversation(Integer conversationId, String username) {
        var conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // check if user is in the conversation
        if (!conversation.get().getAccounts().contains(user.get())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in the conversation");
        }

        try {
            conversation.get().getAccounts().remove(user.get());
            conversationRepository.save(conversation.get());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error removing user from conversation");
        }
    }

    public void addMessageToConversation(ConversationController.AddMessageRequest request, String username) {
        var conversation = conversationRepository.findById(request.conversationId);
        if (conversation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }

        var found = accountRepository.findByUsername(username);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        try {
            var message = new Message();
            message.setContent(request.content);
            message.setAccount(found.get());
            message.setConversation(conversation.get());
            messageRepository.save(message);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error adding message to conversation");
        }
    }

    public Conversation getConversation(Integer id, String username) {
        var conversation = conversationRepository.findById(id);
        if (conversation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // check if the user with the given username is a participant in the conversation with the given id
        if (!conversation.get().getAccounts().contains(user.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a participant in this conversation");
        }

        return conversation.get();
    }


}
