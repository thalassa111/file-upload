package com.me.chengspringboot.repositories;

import com.me.chengspringboot.models.Folder;
import com.me.chengspringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Integer> {

    boolean existsByNameAndUser(String folderName, User user);
}
