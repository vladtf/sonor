package com.pweb.backend.services;

import com.pweb.backend.dao.entities.Message;
import com.pweb.backend.dao.repositories.MessageRepository;
import com.pweb.backend.dao.repositories.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public void deleteMessage(Integer id, String username) {
        // delete the message with the given id if the user with the given username is the author of the message
        var message = messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }

        var user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!message.get().getAccount().equals(user.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this message");
        }

        try {
            messageRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
    }

    public Page<Message> getRecentMessages(String username, Pageable pageable) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
        }

        if (!accountRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return messageRepository.findAllByAccountUsername(username, pageable);
    }
}
