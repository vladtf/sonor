package com.pweb.backend.services;

import com.pweb.backend.controllers.ConversationController;
import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.dao.entities.Message;
import com.pweb.backend.dao.repositories.ConversationRepository;
import com.pweb.backend.dao.repositories.MessageRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteConversation(Integer id, String username) {
        var conversation = conversationRepository.findById(id);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // delete the conversation with the given id if the user with the given username is a participant in the conversation
        if (!conversation.get().getUsers().contains(user.get())) {
            throw new IllegalArgumentException("User is not a participant in the conversation");
        }

        // delete messages from the conversation
        messageRepository.deleteByConversationId(id);
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

        // check if user is already in the conversation
        if (conversation.get().getUsers().contains(user.get())) {
            throw new IllegalArgumentException("User is already in the conversation");
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

    public Conversation addMessageToConversation(ConversationController.AddMessageRequest request, String username) {
        var conversation = conversationRepository.findById(request.conversationId);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // create a new message with the given content and add it to the conversation with the given id
        var message = new Message();
        message.setContent(request.content);
        message.setUser(found.get());
        message.setConversation(conversation.get());
        messageRepository.save(message);

        return conversation.get();
    }

    public Conversation getConversation(Integer id, String username) {
        var conversation = conversationRepository.findById(id);
        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // check if the user with the given username is a participant in the conversation with the given id
        if (!conversation.get().getUsers().contains(user.get())) {
            throw new IllegalArgumentException("You are not a participant in this conversation");
        }

        return conversation.get();
    }
}
