package com.me.chengspringboot.repositories;

import com.me.chengspringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findById(int id);
}
