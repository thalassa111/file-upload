package com.me.chengspringboot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(schema = "chengspringboot")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fileName;

    @Lob
    private byte[] fileContent;

    //Reference to the folder that this file belong to
    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;
}
