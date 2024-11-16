package com.pweb.backend.dao.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "conversations")
public class Conversation {

    public static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("createdAt"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private Collection<Message> messages = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Account> accounts = new java.util.ArrayList<>();

    @PreRemove
    public void preRemove() {
        for (Account account : accounts) {
            if (account != null){
                account.getConversations().remove(this);
            }
        }
    }

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

    public Collection<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Collection<Account> accounts) {
        this.accounts = accounts;
    }

    public Conversation() {
    }

    public Conversation(Collection<Account> accounts, String name) {
        this.accounts = accounts;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
