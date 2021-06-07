package com.example.finalProject.controller;

import com.example.finalProject.object.User;
import com.example.finalProject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody User user) {
        this.userService.createUser(user);
    }

    @PostMapping("/checkUser")
    public User checkUser(@RequestBody User user) {
        return this.userService.validateUser(user.getEmail(), user.getPassword());
    }
}
