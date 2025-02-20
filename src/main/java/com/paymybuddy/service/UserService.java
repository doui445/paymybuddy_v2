package com.paymybuddy.service;

import com.paymybuddy.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

}
