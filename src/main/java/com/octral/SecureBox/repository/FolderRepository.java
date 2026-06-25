package com.octral.SecureBox.repository;

import com.octral.SecureBox.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder,Long> {

    List<Folder> findByUserIdAndParentIsNull(Long userId);

    List<Folder> findByUserId(Long userId);
}
