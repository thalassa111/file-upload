package com.me.chengspringboot.controllers;

import com.me.chengspringboot.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/file/upload-file/{folderId}")
    public ResponseEntity<String> uploadFile(@RequestHeader("Authorization") String token,
                                             @PathVariable int folderId,
                                             @RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(token, folderId, file);
    }

    @DeleteMapping("/file/delete-file/{folderId}/{fileName}")
    public ResponseEntity<String> deleteFile(@RequestHeader("Authorization") String token,
                                             @PathVariable int folderId,
                                             @PathVariable String fileName) {
        return fileService.deleteFile(token, folderId, fileName);
    }

    @GetMapping("/file/download-file/{folderId}/{fileName}")
    public ResponseEntity<Resource> downloadFile( @RequestHeader("Authorization") String token,
                                                @PathVariable int folderId,
                                                @PathVariable String fileName) {
        return fileService.downloadFile(token, folderId, fileName);
    }

}
