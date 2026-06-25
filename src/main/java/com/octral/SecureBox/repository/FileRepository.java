package com.octral.SecureBox.repository;

import com.octral.SecureBox.model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileMetaData,Long> {

    List<FileMetaData> findByUserId(Long userId);

    List<FileMetaData> findByFolderId(Long folderId);
}
