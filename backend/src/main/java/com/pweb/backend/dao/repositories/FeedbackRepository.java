package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Feedback;
import com.pweb.backend.dao.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {
    Page<Feedback> findAll(Pageable pageable);
}
