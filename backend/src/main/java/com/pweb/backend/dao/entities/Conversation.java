package com.pweb.backend.dao.entities;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private Collection<Message> messages = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection <User> users = new java.util.ArrayList<>();

    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Conversation() {
    }

    public Conversation(Collection<Message> messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
