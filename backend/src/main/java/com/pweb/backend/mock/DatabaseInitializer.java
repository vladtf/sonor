package com.pweb.backend.mock;

import com.pweb.backend.dao.entities.*;
import com.pweb.backend.dao.repositories.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final AccountRepository accountRepository;
    /*private final RoleRepository roleRepository;*/
    private final CommentRepository commentRepository;
    private final ConversationRepository conversationRepository;
    private final FeedbackRepository feedbackRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;

    /*private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();*/

    public DatabaseInitializer(AccountRepository accountRepository, /*RoleRepository roleRepository,*/ CommentRepository commentRepository, ConversationRepository conversationRepository, FeedbackRepository feedbackRepository, MessageRepository messageRepository, PostRepository postRepository/*, PasswordEncoder passwordEncoder*/) {
        this.accountRepository = accountRepository;
        /*this.roleRepository = roleRepository;*/
        this.commentRepository = commentRepository;
        this.conversationRepository = conversationRepository;
        this.feedbackRepository = feedbackRepository;
        this.messageRepository = messageRepository;
        this.postRepository = postRepository;
    }


    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        deleteAll();
        provisionUsers();
        provisionPostsToUser();
        provisionConversation();
        provisionFeedback();
    }

    private void provisionFeedback() {
        var user = accountRepository.findByUsername("user").orElseThrow();
        var feedbacks = List.of(
                new Feedback("This is a great app", user, "5", "Feature 1"),
                new Feedback("Feature 2 is not working", user, "3", "Feature 2")
        );

        feedbackRepository.saveAll(feedbacks);
    }

    private void provisionUsers() {
        Account admin = new Account();
        admin.setUsername("admin");
        /*admin.setPassword(passwordEncoder.encode("admin"));  // Use SHA-256 method*/
        accountRepository.save(admin);

        admin = accountRepository.findByUsername("admin").orElseThrow();
        /*roleRepository.save(new Role(Role.RoleEnum.USER, admin));
        roleRepository.save(new Role(Role.RoleEnum.ADMIN, admin));*/


        Account account = new Account();
        account.setUsername("user");
        /*user.setPassword(passwordEncoder.encode("user"));  // Use SHA-256 method*/
        accountRepository.save(account);

        account = accountRepository.findByUsername("user").orElseThrow();
        /*roleRepository.save(new Role(Role.RoleEnum.USER, account));*/

        for (int i = 0; i < 5; i++) {
            account = new Account();
            account.setUsername("user" + i);
            /*user.setPassword(passwordEncoder.encode("user" + i));  // Use SHA-256 method*/
            accountRepository.save(account);
            account = accountRepository.findByUsername("user" + i).orElseThrow();
            /*roleRepository.save(new Role(Role.RoleEnum.USER, account));*/
        }
    }

    private void provisionPostsToUser() {
        var user = accountRepository.findByUsername("user").orElseThrow();
        var posts = List.of(
                new Post("My First Post", "This is my first post", Post.PostCategory.OTHER, user),
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
        var user = accountRepository.findByUsername("user").orElseThrow();
        var admin = accountRepository.findByUsername("admin").orElseThrow();
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
        accountRepository.deleteAll();
        /*roleRepository.deleteAll();*/
        commentRepository.deleteAll();
        conversationRepository.deleteAll();
        feedbackRepository.deleteAll();
        messageRepository.deleteAll();
        postRepository.deleteAll();
    }
}

