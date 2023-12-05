package com.me.chengspringboot.services;

import com.me.chengspringboot.models.File;
import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.repositories.FileRepository;
import com.me.chengspringboot.repositories.FolderRepository;
import com.me.chengspringboot.utilities.JwtUtil;
import com.me.chengspringboot.utilities.StringUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
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
    //method uploads a file, which is sent in as third argument. First argument is token, and second is the id of the folder to upload to
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
                    //loop until the fileNameExists doesnt exist anymore in the folder, then continue with the name adding (x) to the filename
                    while(fileNameExists){
                        count++;
                        fileNameExists = fileRepository.existsByFileNameAndFolder(StringUtil.getNewFileName(fileName, count), folder);
                        System.out.println("filename is taking, count: " + count);
                    }
                    //if nothing happened, no filename conflict, skip this and file will keep the initial filename, or else give a new name with the added (x)
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

    //method for deleting a file, the filename is sent in as third argument, and first one is token, and second one is the folderId of the file
    public ResponseEntity<String> deleteFile(String token, int folderId, String fileName) {
        //verify if token is valid
        if(JwtUtil.verifyToken(token)){
            //get id from token
            String userId = JwtUtil.getSubjectFromToken(token);
            Optional<Folder> optionalFolder = folderRepository.findByIdAndUser_Id(folderId, Integer.parseInt(userId));
            // Verify if the folder with correct ID exists
            if (optionalFolder.isPresent()) {
                Folder folder = optionalFolder.get();
                //check if file exists in the current folder
                if(fileRepository.existsByFileNameAndFolder(fileName, folder)){
                    //delete the file
                    fileRepository.deleteFileByFileName(fileName);
                    return ResponseEntity.ok("file -> " + fileName + " from folder with id -> " + folderId +" has been deleted successfully");
                } else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found on current user");
                }
            } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found for the current user");
            }
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is not valid");
        }
    }

    @Transactional
    public ResponseEntity<Resource> downloadFile(String token, int folderId, String fileName) {
        //verify if token is valid
        if(JwtUtil.verifyToken(token)){
            //get id from token
            String userId = JwtUtil.getSubjectFromToken(token);
            Optional<Folder> optionalFolder = folderRepository.findByIdAndUser_Id(folderId, Integer.parseInt(userId));
            // Verify if the folder with correct ID exists
            if (optionalFolder.isPresent()) {
                // Check if the file exists in the current folder
                /*Optional<File> optionalFile = fileRepository.findByFileNameAndFolder(fileName, optionalFolder);*/
                Optional<File> optionalFile = null;
                try {
                optionalFile = fileRepository.findByFileName(fileName);
            } catch (Exception e) {
                    e.printStackTrace(); // Log or print the exception details
                }
                //check if file is there
                if(optionalFile.isPresent()){
                    File file = optionalFile.get();
                    //get content from database
                    byte[] fileContent = file.getFileContent();
                    //create a ByteArrayResource with the file content
                    ByteArrayResource resource = new ByteArrayResource(fileContent);
                    //content type detection based on file extension
                    String contentType = URLConnection.guessContentTypeFromName(fileName);
                    //wanna be able to download any kind of file
                    MediaType mediaType = MediaType.parseMediaType(contentType);
                    //Content-Disposition header
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(fileContent.length)
                            .contentType(mediaType)
                            .body(resource);

                    } else{
                    return ResponseEntity.notFound().build();
                    }
            } else {
                return ResponseEntity.notFound().build();
            }
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
