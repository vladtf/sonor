package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Comment;
import com.pweb.backend.dao.entities.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    Collection<Message> findAllByUserUsername(String username);

    void deleteByConversationId(Integer id);
}
