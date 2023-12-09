package com.me.chengspringboot.repositories;

import com.me.chengspringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    Optional<User> findById(int id);
}
