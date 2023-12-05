package com.me.chengspringboot.controllers;

import com.me.chengspringboot.dtos.LoginRequestDto;
import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    //register a new user
    @PostMapping("/user/register")
    public ResponseEntity<String> addUser(@RequestBody UserDto userDto) {
        User tmp = userService.createCustomer(userDto);
        return ResponseEntity.ok("customer added: " + tmp.getName());
    }

    //will generate a token for a user who provide a correct email and password
    @GetMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(userService.generateTokenForUserByEmailAndPassword(loginRequestDto.email, loginRequestDto.password));
    }

    //used to verify the token, nice to have for testing
    @GetMapping("/user/verify-token")
    public String verifyToken(@RequestParam String token) {
        return userService.verifyToken(token);
    }

    @GetMapping("/user/get-user")
    public ResponseEntity<User> getUser(@RequestParam String token) {
        return ResponseEntity.ok(userService.getUserByToken(token));
    }
}