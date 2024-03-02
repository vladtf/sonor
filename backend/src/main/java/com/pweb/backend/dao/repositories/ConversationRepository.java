package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Conversation;
import com.pweb.backend.dao.entities.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    Collection<Conversation> findAllByUsersUsername(String username);
}
