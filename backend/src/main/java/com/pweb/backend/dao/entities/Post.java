package com.pweb.backend.dao.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;


    private String title;

    private String content;


    private PostCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PreRemove
    public void preRemove() {
        user.getPosts().remove(this);
    }

    public Post() {
    }

    public Post(String title, String content, PostCategory category, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory postCategory) {
        this.category = postCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public static enum PostCategory {
        SPORT, POLITICS, SCIENCE, TECHNOLOGY, ART, MUSIC, MOVIES, GAMES, TRAVEL, FOOD, FASHION, HEALTH, EDUCATION, BUSINESS, FINANCE, OTHER
    }


}
