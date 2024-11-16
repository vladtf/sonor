package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    Page<Conversation> findAllByAccountsUsername(String username, Pageable pageable);

    Page<Conversation> findAllByNameContainingAndAccountsUsername(String searchTerm, String username, Pageable pageable);
}
