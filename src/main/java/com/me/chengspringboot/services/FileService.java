package com.me.chengspringboot.services;

import com.me.chengspringboot.exceptions.FileNotFoundException;
import com.me.chengspringboot.exceptions.FolderNotFoundException;
import com.me.chengspringboot.exceptions.UnauthorizedTokenException;
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

import java.net.URLConnection;
import java.util.Optional;

@Service
public class FileService {

    public FileRepository fileRepository;
    public FolderRepository folderRepository;
    public JwtUtil jwtUtil;

    @Autowired
    public FileService(FileRepository fileRepository, FolderRepository folderRepository, JwtUtil jwtUtil) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.jwtUtil = jwtUtil;
    }

    //method uploads a file, which is sent in as third argument. First argument is token, and second is the id of the folder to upload to
    public ResponseEntity<String> uploadFile(String token, int folderId, MultipartFile file) {
        try {
            //verify if token is valid
            validateToken(token);

            //extract user ID from token
            String userId = jwtUtil.getSubjectFromToken(token);

            //get the folder by ID and user ID
            Folder folder = getFolderByIdAndUserId(folderId, Integer.parseInt(userId));

            //check for filename conflicts and handle them
            String fileName = handleFileNameConflict(file, folder);

            //save file to the database
            File fileEntity = new File();
            fileEntity.setFileName(fileName);
            fileEntity.setFileContent(file.getBytes());
            fileEntity.setFolder(folder);
            fileRepository.save(fileEntity);

            return ResponseEntity.ok("File: " + fileEntity.getFileName() + " uploaded successfully to folder: " + folderId);
        } catch (UnauthorizedTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (FolderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    //method for deleting a file, the filename is sent in as third argument, and first one is token, and second one is the folderId of the file
    public ResponseEntity<String> deleteFile(String token, int folderId, String fileName) {
        try {
            //verify if token is valid
            validateToken(token);

            //extract user ID from token
            String userId = jwtUtil.getSubjectFromToken(token);

            //get the folder by ID and user ID
            Folder folder = getFolderByIdAndUserId(folderId, Integer.parseInt(userId));

            //check if the file exists in the current folder
            deleteFileFromFolder(fileName, folder);

            //if everything checks out, return ok message
            return ResponseEntity.ok("File -> " + fileName + " from folder with ID -> " + folderId + " has been deleted successfully");
        } catch (UnauthorizedTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (FolderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + e.getMessage());
        }
    }

    //downloads a file, gets userID from token, then the folderId is sent in and the filename, returns the file as a Resource
    @Transactional
    public ResponseEntity<Resource> downloadFile(String token, int folderId, String fileName) {
        try {
            //verify if token is valid
            validateToken(token);

            //Extract user ID from token
            String userId = jwtUtil.getSubjectFromToken(token);

            //Get the folder by ID and user ID
            Optional<Folder> optionalFolder = folderRepository.findByIdAndUser_Id(folderId, Integer.parseInt(userId));
            Folder folder = optionalFolder.orElseThrow(() -> new FolderNotFoundException("Folder not found for the current user"));

            //get the file from the database
            File file = getFileByNameAndFolder(fileName, optionalFolder);

            //get content from the database
            byte[] fileContent = file.getFileContent();

            //create a ByteArrayResource with the file content
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            //content type detection based on file extension
            String contentType = URLConnection.guessContentTypeFromName(fileName);

            //want to be able to download any kind of file
            MediaType mediaType = MediaType.parseMediaType(contentType);

            //content-Disposition header
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            //returns the file as a Resource
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileContent.length)
                    .contentType(mediaType)
                    .body(resource);

        } catch (UnauthorizedTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (FolderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //validation of token
    private void validateToken(String token) {
        if (!jwtUtil.verifyToken(token)) {
            throw new UnauthorizedTokenException("Token is not valid");
        }
    }

    //will create a new filename, adding a number x, like this: file.txt -> file(x).txt
    private String handleFileNameConflict(MultipartFile file, Folder folder) {
        boolean fileNameExists = fileRepository.existsByFileNameAndFolder(file.getOriginalFilename(), folder);
        int count = 0;
        String fileName = file.getOriginalFilename();

        //will stay here until a valid name is generated
        while (fileNameExists) {
            count++;
            fileNameExists = fileRepository.existsByFileNameAndFolder(StringUtil.getNewFileName(fileName, count), folder);
            System.out.println("filename is taken, count: " + count);
        }

        //if nothing happened, no filename conflict, skip this and file will keep the initial filename, or else give a new name with the added (x)
        if (count != 0) {
            fileName = StringUtil.getNewFileName(file.getOriginalFilename(), count);
        }

        return fileName;
    }

    private Folder getFolderByIdAndUserId(int folderId, int userId) {
        Optional<Folder> optionalFolder = folderRepository.findByIdAndUser_Id(folderId, userId);
        if (optionalFolder.isPresent()) {
            return optionalFolder.get();
        } else {
            throw new FolderNotFoundException("Folder not found for the current user");
        }
    }

    private void deleteFileFromFolder(String fileName, Folder folder) {
        if (fileRepository.existsByFileNameAndFolder(fileName, folder)) {
            //the file deleting using repo
            fileRepository.deleteFileByFileName(fileName);
        } else {
            throw new FileNotFoundException("File not found in the current folder");
        }
    }

    private File getFileByNameAndFolder(String fileName, Optional<Folder> optionalFolder) {
        if (optionalFolder.isPresent()) {
            Optional<File> optionalFile = fileRepository.findByFileNameAndFolder(fileName, optionalFolder);
            if (optionalFile.isPresent()) {
                return optionalFile.get();
            } else {
                throw new FileNotFoundException("File not found in the current folder");
            }
        } else {
            throw new FolderNotFoundException("Folder not found for the current user");
        }
    }
}