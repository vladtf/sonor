package com.pweb.backend.services;

import com.pweb.backend.controllers.ConversationController;
import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.dao.entities.Message;
import com.pweb.backend.dao.repositories.ConversationRepository;
import com.pweb.backend.dao.repositories.MessageRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ConversationService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    public ConversationService(MessageRepository messageRepository, UserRepository userRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }


    public Collection<Conversation> getAllConversations(String username) {
        // get all conversations for the user with the given username
        return conversationRepository.findAllByUsersUsername(username);
    }

    public Conversation createConversation(String username, String name) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // create a new conversation with the given name and add the user with the given username to it
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.getUsers().add(user.get());
        conversationRepository.save(conversation);

        return conversation;
    }

    public void deleteConversation(Integer id) {
        // delete the conversation with the given id
        conversationRepository.deleteById(id);
    }

    public Conversation addUserToConversation(Integer conversationId, String username) {
        var conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // add the user with the given username to the conversation with the given id
        conversation.get().getUsers().add(user.get());
        conversationRepository.save(conversation.get());

        return conversation.get();
    }

    public Conversation removeUserFromConversation(Integer conversationId, String username) {
        var conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // remove the user with the given username from the conversation with the given id
        conversation.get().getUsers().remove(user.get());
        conversationRepository.save(conversation.get());

        return conversation.get();
    }

    public Conversation addMessageToConversation(ConversationController.AddMessageRequest request) {
        var conversation = conversationRepository.findById(request.conversationId);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var user = userRepository.findByUsername(request.username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // create a new message with the given content and add it to the conversation with the given id
        var message = new Message();
        message.setContent(request.content);
        message.setUser(user.get());
        message.setConversation(conversation.get());
        messageRepository.save(message);

        return conversation.get();
    }
}
