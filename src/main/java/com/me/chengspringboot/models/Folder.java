package com.me.chengspringboot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(schema = "chengspringboot")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    public Folder(String name, User user)  {
        this.name = name;
        this.user = user;
    }

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}