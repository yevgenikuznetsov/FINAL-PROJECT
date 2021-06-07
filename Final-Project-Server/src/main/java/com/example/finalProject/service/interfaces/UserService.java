package com.example.finalProject.service.interfaces;

import com.example.finalProject.object.User;

public interface UserService {
    User validateUser(String email, String password);

    void createUser(User user);
}
