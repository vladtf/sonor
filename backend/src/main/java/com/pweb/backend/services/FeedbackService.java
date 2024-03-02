package com.pweb.backend.services;

import com.pweb.backend.controllers.FeedbackController;
import com.pweb.backend.dao.entities.Feedback;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.FeedbackRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Feedback createFeedback(FeedbackController.CreateFeedbackRequest feedback, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Feedback newFeedback = new Feedback();
        newFeedback.setContent(feedback.content);
        newFeedback.setSatisfaction(feedback.satisfaction);
        newFeedback.setFeature(feedback.feature);

        newFeedback.setUser(user.get());
        return feedbackRepository.save(newFeedback);
    }

    public void deleteFeedback(Integer id, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isEmpty()) {
            throw new IllegalArgumentException("Feedback not found");
        }

        if (!feedback.get().getUser().equals(user.get())) {
            throw new IllegalArgumentException("User not allowed to delete this feedback");
        }

        feedbackRepository.delete(feedback.get());
    }
}
