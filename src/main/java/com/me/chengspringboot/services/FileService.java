package com.me.chengspringboot.services;

import com.me.chengspringboot.models.File;
import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.repositories.FileRepository;
import com.me.chengspringboot.repositories.FolderRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import com.me.chengspringboot.utilities.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FileService {

    private FileRepository fileRepository;
    private FolderRepository folderRepository;

    @Autowired
    public FileService(FileRepository fileRepository, FolderRepository folderRepository){
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
    }

    public ResponseEntity<String> uploadFile(String token, int folderId, MultipartFile file) {
        //verify if token if valid
        if (JwtUtil.verifyToken(token)) {
            try {
                //get id from token
                String userId = JwtUtil.getSubjectFromToken(token);
                Optional<Folder> optionalFolder = folderRepository.findByIdAndUser_Id(folderId, Integer.parseInt(userId));
                // Verify if the folder with correct ID exists
                if (optionalFolder.isPresent()) {
                    Folder folder = optionalFolder.get();
                    //check if the filename already exists in the folder
                    boolean fileNameExists = fileRepository.existsByFileNameAndFolder(file.getOriginalFilename(), folder);
                    int count = 0;
                    String fileName = file.getOriginalFilename();
                    //loop until the filename doesnt exist anymore in the folder, then continue with the name adding (x) to the filename
                    while(fileNameExists){
                        count++;
                        fileNameExists = fileRepository.existsByFileNameAndFolder(StringUtil.getNewFileName(fileName, count), folder);
                        System.out.println("filename is taking, count: " + count);
                    }
                    //if nothing happened, no filename conflict, skip this and file will keep the initial filename, or else give a new name
                    if(count != 0){ fileName = StringUtil.getNewFileName(file.getOriginalFilename(), count); }
                    //save file to database, max size is 1MB
                    File fileEntity = new File();
                    fileEntity.setFileName(fileName);
                    fileEntity.setFileContent(file.getBytes());
                    fileEntity.setFolder(folder);
                    fileRepository.save(fileEntity);
                    return ResponseEntity.ok("File: " + fileEntity.getFileName() + " uploaded successfully to folder: " + folderId);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found for the current user");
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing the file: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is not valid");
        }
    }
}
