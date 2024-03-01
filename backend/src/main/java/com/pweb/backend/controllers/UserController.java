
package com.pweb.backend.controllers;

import com.pweb.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/emails")
    public List<String> getAllEmails(@RequestHeader("Authorization") String token) {
        return userService.getAllEmails(token);
    }

}

