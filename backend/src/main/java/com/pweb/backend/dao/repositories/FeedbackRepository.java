package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {
    Page<Feedback> findAll(Pageable pageable);
}
