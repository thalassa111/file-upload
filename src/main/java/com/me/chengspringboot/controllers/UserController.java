package com.me.chengspringboot.controllers;

import com.me.chengspringboot.dtos.LoginRequestDto;
import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //register a new user
    @PostMapping("/user/register")
    public ResponseEntity<String> addUser(@RequestBody UserDto userDto){
        userService.createCustomer(userDto);
        return ResponseEntity.ok("customer added: " + userDto.getName());
    }

    //will generate a token for a user who provide a correct email and password
    @GetMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(userService.generateTokenForUserByEmailAndPassword(loginRequestDto.email, loginRequestDto.password));
    }

}
