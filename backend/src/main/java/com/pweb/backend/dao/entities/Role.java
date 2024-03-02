package com.pweb.backend.dao.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private RoleEnum name;

    public enum RoleEnum {
        USER, ADMIN
    }

    public Role() {
    }

    public Role(RoleEnum name, User user) {
        this.name = name;
        this.user = user;
    }

    public Role(RoleEnum name) {
        this.name = name;
    }

    public Role(Integer id, User user, RoleEnum name) {
        this.id = id;
        this.user = user;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleEnum getName() {
        return name;
    }

    public void setName(RoleEnum name) {
        this.name = name;
    }

}
