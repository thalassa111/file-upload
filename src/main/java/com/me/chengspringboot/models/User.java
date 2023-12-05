package com.me.chengspringboot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
//to get the
@Table(schema = "chengspringboot")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    public User(String name, String email, String address, String password, String salt) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = password;
        this.salt = salt;
    }

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String email;

    @Column
    public String address;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String salt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Folder> folders = new ArrayList<>();
}
