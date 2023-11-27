package com.me.chengspringboot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
//to get the
@Table(schema = "chengspringboot")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    public User(String name, String email, String address, String role, String password){
        this.name = name;
        this.email = email;
        this.address = address;
        this.role = role;
        this.password = password;
    }

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String email;

    @Column
    public String address;

    @Column(nullable = false)
    public String role;

    @Column(nullable = false)
    public String password;
}
