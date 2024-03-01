package com.pweb.backend.dao.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

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


//    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
//    @Cascade(org.hibernate.annotations.CascadeType.ALL)
//    private List<Token> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    //@OneToMany(mappedBy = "user")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Role> roles;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getAccounts() {
        return posts;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setAccounts(List<Post> posts) {
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}