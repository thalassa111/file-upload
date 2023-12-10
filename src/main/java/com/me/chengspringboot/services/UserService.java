package com.me.chengspringboot.services;

import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.exceptions.EmailNotFoundException;
import com.me.chengspringboot.exceptions.UserCreationException;
import com.me.chengspringboot.exceptions.WrongPasswordException;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.repositories.UserRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import com.me.chengspringboot.utilities.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    public UserRepository userRepository;
    public PasswordEncoderUtil passwordEncoder;
    public JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoderUtil passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //creates a new user using userDto
    public User createUser(UserDto userDto) {
        try {
            //validate UserDto, dont want any variable to be empty
            validateUserDto(userDto);

            // Using salt for extra security, recommended for one-way password hashing
            String salt = passwordEncoder.generateSalt();
            var user = new User(userDto.getName(),
                    userDto.getEmail(),
                    userDto.getAddress(),
                    passwordEncoder.encodePassword(userDto.getPassword(), salt),
                    salt);
            return this.userRepository.save(user);
        } catch (Exception e) {
            throw new UserCreationException("Error creating customer: " + e.getMessage());
        }
    }

    //checking so all fields in userDto are not empty, if they are, throw exception
    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (userDto.getAddress() == null || userDto.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    //generates a token based on email and password, since email is more unique than a name
    public String generateTokenForUserByEmailAndPassword(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                //will compare the raw password with the hashed one along with the salt, if they match, its ok
                if (passwordEncoder.verifyPassword(password, user.password, user.getSalt())) {
                    return "Generated token: " + JwtUtil.createToken(String.valueOf(user.getId()), user.getName());
                } else {
                    throw new WrongPasswordException("wrong password, cannot generate token");
                }
            } else {
                throw new EmailNotFoundException("Email not found");
            }
        } catch (EmailNotFoundException e) {
            return e.getMessage();
        } catch (WrongPasswordException e) {
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "error when getting email";
        }
    }

    //only used for testing
    public String verifyToken(String token) {
        boolean isValid = jwtUtil.verifyToken(token);
        if (isValid) {
            String id = jwtUtil.getSubjectFromToken(token);
            Optional<User> optionalUser = userRepository.findById(Integer.parseInt(id));
            User user = optionalUser.orElse(null);
            return "Token is valid name: " + user.getName() + "  id: " + user.getId();
        } else {
            return "invalid token";
        }
    }

    public User getUserByToken(String token) {
        //gets id from token
        String subject = jwtUtil.getSubjectFromToken(token);
        Optional<User> optionalUser = userRepository.findById(Integer.parseInt(subject));
        User user = optionalUser.orElse(null);
        return user;
    }
}
