package com.me.chengspringboot.services;

import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createCustomer(UserDto userDto) {
        var customer = new User(userDto.getName(),
                userDto.getEmail(),
                userDto.getAddress(),
                userDto.getPassword());

        return this.userRepository.save(customer);
    }
}
