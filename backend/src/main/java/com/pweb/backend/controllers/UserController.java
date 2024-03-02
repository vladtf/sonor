package com.pweb.backend.controllers;

import com.pweb.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/usernames")
    @Secured("ROLE_USER")
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsernames());
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable).map(user -> {
            if (user == null) {
                return null;
            }

            UserResponse response = new UserResponse();
            response.id = user.getId();
            response.username = user.getUsername();
            response.roles = user.getRoles().stream().map(role -> role.getName().name()).toList();
            response.postCount = user.getPosts().size();
            response.commentCount = user.getComments().size();
            response.messageCount = user.getMessages().size();
            return response;
        }));
    }

    @GetMapping("/search")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<UserResponse>> searchUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam String query) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, pageable).map(user -> {
            if (user == null) {
                return null;
            }

            UserResponse response = new UserResponse();
            response.id = user.getId();
            response.username = user.getUsername();
            response.roles = user.getRoles().stream().map(role -> role.getName().name()).toList();
            response.postCount = user.getPosts().size();
            response.commentCount = user.getComments().size();
            response.messageCount = user.getMessages().size();
            return response;
        }));
    }

    public static class UserResponse {
        public Integer id;
        public String username;
        public Integer postCount;
        public Integer commentCount;
        public Integer messageCount;

        public List<String> roles;
    }


}

