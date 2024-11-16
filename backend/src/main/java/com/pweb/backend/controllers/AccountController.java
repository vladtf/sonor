package com.pweb.backend.controllers;

import com.pweb.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class AccountController {

    private final UserService userService;


    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/usernames")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all usernames",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "200", description = "List of usernames")
            })
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsernames());
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Delete a user",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "200", description = "List of users"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable).map(user -> {
            if (user == null) {
                return null;
            }

            UserResponse response = new UserResponse();
            response.id = user.getId();
            response.username = user.getUsername();
            /*response.roles = user.getRoles().stream().map(role -> role.getName().name()).toList();*/
            response.postCount = user.getPosts().size();
            response.commentCount = user.getComments().size();
            response.messageCount = user.getMessages().size();
            return response;
        }));
    }

    @GetMapping("/search")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Search for users",
            responses = {
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "200", description = "List of users"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Page<UserResponse>> searchUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam String query) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, pageable).map(user -> {
            if (user == null) {
                return null;
            }

            UserResponse response = new UserResponse();
            response.id = user.getId();
            response.username = user.getUsername();
            /*response.roles = user.getRoles().stream().map(role -> role.getName().name()).toList();*/
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

