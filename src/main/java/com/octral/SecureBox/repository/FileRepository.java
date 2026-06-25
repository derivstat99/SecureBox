package com.octral.SecureBox.repository;

import com.octral.SecureBox.model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileMetaData, Long> {

    List<FileMetaData> findByUserIdAndDeletedFalse(Long userId);

    List<FileMetaData> findByFolderIdAndDeletedFalse(Long folderId);

    List<FileMetaData> findByUserIdAndDeletedTrue(Long userId);

    List<FileMetaData> findByUserIdAndFavoriteTrueAndDeletedFalse(Long userId);

    List<FileMetaData> findByUserIdAndFileNameContainingIgnoreCaseAndDeletedFalse(Long userId, String fileName);

    List<FileMetaData> findByUserIdAndDeletedFalseOrderByUploadedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileMetaData f WHERE f.user.id = :userId AND f.deleted = false")
    long sumFileSizeByUserIdAndDeletedFalse(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM FileMetaData f WHERE f.user.id = :userId AND f.deleted = false")
    long countByUserIdAndDeletedFalse(@Param("userId") Long userId);
}
