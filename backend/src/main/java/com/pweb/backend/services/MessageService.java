package com.pweb.backend.services;

import com.pweb.backend.dao.repositories.MessageRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void deleteMessage(Integer id, String username) {
        // delete the message with the given id if the user with the given username is the author of the message
        var message = messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        if (!message.get().getUser().equals(user.get())) {
            throw new IllegalArgumentException("User is not the author of the message");
        }

        messageRepository.deleteById(id);
    }
}
