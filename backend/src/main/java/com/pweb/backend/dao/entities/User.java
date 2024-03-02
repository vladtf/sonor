package com.pweb.backend.dao.entities;


import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Message> messages;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Collection<Conversation> conversations;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Feedback> feedbacks;

    @PreRemove
    private void preRemove() {
        for (Post post : posts) {
            post.setUser(null);
        }
        for (Role role : roles) {
            role.setUser(null);
        }
        for (Comment comment : comments) {
            comment.setUser(null);
        }
        for (Message message : messages) {
            message.setUser(null);
        }
        for (Conversation conversation : conversations) {
            conversation.getUsers().remove(this);
        }

        for (Feedback feedback : feedbacks) {
            feedback.setUser(null);
        }
    }

    public User() {
    }

    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public Collection<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(Collection<Conversation> conversations) {
        this.conversations = conversations;
    }

    public Collection<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Collection<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}