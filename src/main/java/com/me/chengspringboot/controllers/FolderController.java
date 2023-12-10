package com.me.chengspringboot.controllers;

import com.me.chengspringboot.dtos.FolderRequestDto;
import com.me.chengspringboot.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FolderController {

    private FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }
    //creates a folder, takes in a token and folderDto, which is just a name of folder. User extracted from token
    @PostMapping("/folder/create-folder")
    public ResponseEntity<String> createFolder(@RequestHeader("Authorization") String token,
                                               @RequestBody FolderRequestDto folderRequest) {
        return folderService.createFolder(token, folderRequest.getFolderName());
    }
}