package com.pweb.backend.services;

import com.pweb.backend.controllers.FeedbackController;
import com.pweb.backend.dao.entities.Feedback;
import com.pweb.backend.dao.entities.Account;
import com.pweb.backend.dao.repositories.FeedbackRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }


    public Page<Feedback> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    public void createFeedback(FeedbackController.CreateFeedbackRequest feedback, String username) {
        Optional<Account> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        try {
            Feedback newFeedback = new Feedback();
            newFeedback.setContent(feedback.content);
            newFeedback.setSatisfaction(feedback.satisfaction);
            newFeedback.setFeature(feedback.feature);
            newFeedback.setAccount(user.get());
            feedbackRepository.save(newFeedback);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
    }

    public void deleteFeedback(Integer id, String username) {
        Optional<Account> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found");
        }

        if (!feedback.get().getAccount().equals(user.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this feedback");
        }

        try {
            feedbackRepository.delete(feedback.get());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting feedback");
        }
    }
}
