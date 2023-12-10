package com.me.chengspringboot.services;

import com.me.chengspringboot.exceptions.FolderAlreadyExistsException;
import com.me.chengspringboot.exceptions.InvalidFolderInputException;
import com.me.chengspringboot.exceptions.UnauthorizedTokenException;
import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.repositories.FolderRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private UserService userService;
    private JwtUtil jwtUtil;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserService userService, JwtUtil jwtUtil) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //for creating a folder, takes in token and the name of folder to be created
    public ResponseEntity<String> createFolder(String token, String folderName) {
        try {
            //check if token is ok, and folderName exists
            validateFolderCreationInput(token, folderName);

            //extract user from token
            User user = userService.getUserByToken(token);

            //check if there is a folder with user and folder, if its ok, create folder
            if (!folderRepository.existsByNameAndUser(folderName, user)) {
                Folder folder = new Folder(folderName.trim(), user);

                //save to repo
                folderRepository.save(folder);
                return ResponseEntity.ok("Folder created: " + folder.getName());
            } else {
                throw new FolderAlreadyExistsException("Folder with the same name already exists");
            }
        } catch (InvalidFolderInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnauthorizedTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (FolderAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error when creating folder");
        }
    }

    private void validateFolderCreationInput(String token, String folderName) {
        if (!StringUtils.hasText(folderName)) {
            throw new InvalidFolderInputException("Folder name cannot be null or empty");
        }
        if (!jwtUtil.verifyToken(token)) {
            throw new UnauthorizedTokenException("Token is not valid");
        }
    }
}