package com.pweb.backend.dao.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer id;

    private String content;

    private String satisfaction;

    private String feature;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PreRemove
    public void preRemove() {
        if (user != null) {
            user.getFeedbacks().remove(this);
        }
    }

    public Feedback() {
    }

    public Feedback(String content, User user, String satisfaction, String feature) {
        this.content = content;
        this.user = user;
        this.satisfaction = satisfaction;
        this.feature = feature;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public void setFeature(String feature) {
        this.feature = feature;

    }

    public String getFeature() {
        return feature;
    }
}
