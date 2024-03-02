package com.pweb.backend.mock;

import com.pweb.backend.dao.entities.*;
import com.pweb.backend.dao.repositories.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;
    private final ConversationRepository conversationRepository;
    private final FeedbackRepository feedbackRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepository, RoleRepository roleRepository, CommentRepository commentRepository, ConversationRepository conversationRepository, FeedbackRepository feedbackRepository, MessageRepository messageRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.commentRepository = commentRepository;
        this.conversationRepository = conversationRepository;
        this.feedbackRepository = feedbackRepository;
        this.messageRepository = messageRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        deleteAll();
        provisionUsers();
        provisionPostsToUser();
        provisionConversation();
    }

    private void provisionUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);

        admin = userRepository.findByUsername("admin").orElseThrow();
        roleRepository.save(new Role(Role.RoleEnum.USER, admin));
        roleRepository.save(new Role(Role.RoleEnum.ADMIN, admin));


        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        userRepository.save(user);

        user = userRepository.findByUsername("user").orElseThrow();
        roleRepository.save(new Role(Role.RoleEnum.USER, user));
    }

    private void provisionPostsToUser() {
        var user = userRepository.findByUsername("user").orElseThrow();
        var posts = List.of(
                new Post("Title 1", "Content 1", Post.PostCategory.OTHER, user),
                new Post("Title 2", "Content 2", Post.PostCategory.OTHER, user),
                new Post("Title 3", "Content 3", Post.PostCategory.OTHER, user),
                new Post("Title 4", "Content 4", Post.PostCategory.OTHER, user),
                new Post("Title 5", "Content 5", Post.PostCategory.OTHER, user),
                new Post("Title 6", "Content 6", Post.PostCategory.OTHER, user)
                );

        postRepository.saveAll(posts);
    }

    private void provisionConversation() {
        var user = userRepository.findByUsername("user").orElseThrow();
        var admin = userRepository.findByUsername("admin").orElseThrow();
        var conversation = new Conversation(List.of(user, admin), "User-Admin Conversation");
        conversationRepository.save(conversation);

        var message = new Message("Hello", user, conversation);
        messageRepository.save(message);

        message = new Message("Hi", admin, conversation);
        messageRepository.save(message);

        for (int i = 0; i < 10; i++) {
            message = new Message("Message " + i, user, conversation);
            messageRepository.save(message);
        }

        for (int i = 0; i < 10; i++) {
            var conversation2 = new Conversation(List.of(user), "User Conversation " + i);
            conversationRepository.save(conversation2);
        }
    }


    private void deleteAll() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        commentRepository.deleteAll();
        conversationRepository.deleteAll();
        feedbackRepository.deleteAll();
        messageRepository.deleteAll();
        postRepository.deleteAll();
    }
}

