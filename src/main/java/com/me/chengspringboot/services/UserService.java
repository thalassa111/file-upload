package com.me.chengspringboot.services;

import com.me.chengspringboot.dtos.UserDto;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.repositories.UserRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import com.me.chengspringboot.utilities.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoderUtil passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoderUtil passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //creates a new user using userDto
    public User createCustomer(UserDto userDto) {
        //using salt for extra security, which is recommended for this type of one way password
        String salt = BCrypt.gensalt();
        var customer = new User(userDto.getName(),
                userDto.getEmail(),
                userDto.getAddress(),
                passwordEncoder.encodePassword(userDto.getPassword(), salt),
                salt);
        return this.userRepository.save(customer);
    }

    //generates a token based on email and password, since email is more unique than a name
    public String generateTokenForUserByEmailAndPassword(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                //will compare the raw password with the hashed one along with the salt, if they match, its ok
                if (passwordEncoder.verifyPassword(password, user.password, user.getSalt())) {
                    return "Generated token: " + JwtUtil.createToken(String.valueOf(user.getId()), user.getName());
                }
            } else {
                return "email not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error when getting email";
        }
        return "wrong password";
    }

    public String verifyToken(String token) {
        boolean isValid = JwtUtil.verifyToken(token);
        if (isValid) {
            String id = JwtUtil.getSubjectFromToken(token);
            User user = userRepository.findById(Integer.parseInt(id));
            return "Token is valid name: " + user.getName() + "  id: " + user.getId();
        } else {
            return "invalid token";
        }
    }

    public boolean verifyTokenBoolean(String token) {
        boolean isValid = JwtUtil.verifyToken(token);
        if (isValid) {
            String id = JwtUtil.getSubjectFromToken(token);
            User user = userRepository.findById(Integer.parseInt(id));
            return true;
        } else {
            return false;
        }
    }

    public User getUserByToken(String token) {
        //gets id from token
        String subject = JwtUtil.getSubjectFromToken(token);
        return userRepository.findById(Integer.parseInt(subject));
    }
}
