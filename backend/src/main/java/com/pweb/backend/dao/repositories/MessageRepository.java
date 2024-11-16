package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    Page<Message> findAllByAccountUsername(String username, Pageable pageable);

    void deleteByConversationId(Integer id);
}
