package com.bipedalprogrammer.journal.web.controller;

import com.bipedalprogrammer.journal.web.repository.UserPersistence;
import com.bipedalprogrammer.journal.web.security.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("/api/registration")
public class RegistrationController {
    private UserPersistence userRepository;

    @Autowired
    public RegistrationController(UserPersistence userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(value="username", defaultValue="guest") String username) {
        List<User> users = new ArrayList<>();
        if (username != null) {
            User user = userRepository.findByUsername(username);
            if (!user.isEnabled()) {
                return new ResponseEntity<List<User>>(users, HttpStatus.NOT_FOUND);
            } else {
                users.add(user);
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(users, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> addUser(@RequestBody AddUserRequest addUser) {
        User user = new User(addUser.getUsername(), addUser.getPassword(), true);
        ApiResponse response = new ApiResponse();
        if (userRepository.addUser(addUser.getUsername(), addUser.getPassword())) {
            response.setStatus(ApiResponse.STATUS_SUCCESSFUL);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            response.setStatus(ApiResponse.STATUS_ERROR);
            response.setMessage("Does user already exist?");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
