package com.me.chengspringboot.services;

import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.repositories.FolderRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private UserService userService;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserService userService) {
        this.folderRepository = folderRepository;
        this.userService = userService;
    }

    //for creating a folder, takes in token and the name of folder to be created
    public ResponseEntity<String> createFolder(String token, String folderName) {
        //check if token is a valid one
        if (JwtUtil.verifyToken(token)) {
            //extract the user from token
            User user = userService.getUserByToken(token);
            //last one is for checking if there are folders with same name, dont want that
            if (folderName != null && !folderName.isEmpty() && !folderRepository.existsByNameAndUser(folderName, user)) {
                try {
                    //creates a new folder, using the provided foldername, and then get a user through the token
                    var folder = new Folder(folderName, user);
                    //save to repo
                    this.folderRepository.save(folder);
                    return ResponseEntity.ok("Folder created: " + folder.getName());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error creating folder: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Folder name cannot be null, empty or have the same name");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is not valid");
        }
    }
}