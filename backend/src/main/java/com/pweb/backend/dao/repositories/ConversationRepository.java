package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    Page<Conversation> findAllByUsersUsername(String username, Pageable pageable);

    Page<Conversation> findAllByNameContainingAndUsersUsername(String searchTerm, String username, Pageable pageable);
}
