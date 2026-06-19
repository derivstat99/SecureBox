package com.octral.SecureBox.repository;

import com.octral.SecureBox.model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileMetaData,Long> {
}
