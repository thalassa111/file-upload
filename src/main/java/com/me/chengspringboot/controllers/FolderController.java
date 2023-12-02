package com.me.chengspringboot.controllers;

import com.me.chengspringboot.dtos.FolderRequestDto;
import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.models.User;
import com.me.chengspringboot.services.FolderService;
import com.me.chengspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FolderController {

    private UserService userService;
    private FolderService folderService;

    @Autowired
    public FolderController(UserService userService, FolderService folderService){
        this.userService = userService;
        this.folderService = folderService;
    }

    @PostMapping("/folder/create-folder")
    public ResponseEntity<String> createFolder(@RequestHeader("Authorization") String token, @RequestBody FolderRequestDto folderRequest){
        /*String response = folderService.createFolder(token, folderRequest.getFolderName());*/
        return folderService.createFolder(token, folderRequest.getFolderName());
    }

/*    @PostMapping("/folder/upload-file/{folderID}")
    public ResponseEntity<String> uploadFile(@RequestHeader("Authorization") String token,
                                             @PathVariable int folderId,
                                             @RequestParam("file")MultipartFile file){
        return folderService.uploadFile(token, folderId, file);
    }*/

}
