package com.me.chengspringboot.repositories;

import com.me.chengspringboot.models.File;
import com.me.chengspringboot.models.Folder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<File, Integer> {
    boolean existsByFileNameAndFolder(String originalFilename, Folder folder);
    @Transactional
    void deleteFileByFileName(String fileName);
}
