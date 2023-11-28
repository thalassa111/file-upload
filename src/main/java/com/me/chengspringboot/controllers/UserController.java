package com.me.chengspringboot.controllers;

import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/user/register")
    public ResponseEntity<String> addUser(@RequestBody UserDto userDto){
        userService.createCustomer(userDto);
        return ResponseEntity.ok("customer added: " + userDto.getName());
    }

}
