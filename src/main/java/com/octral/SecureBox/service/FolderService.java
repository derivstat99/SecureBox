package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.FolderResponse;
import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    public Folder addFolder(String name, User user, Long parentId) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUser(user);

        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            if (!parent.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }
            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> getFolderHierarchy(User user) {
        return folderRepository.findByUserIdAndParentIsNull(user.getId()).stream()
                .map(FolderResponse::from)
                .collect(Collectors.toList());
    }

    public Folder renameFolder(Long folderId, String newName, User user) {
        Folder folder = getOwnedFolder(folderId, user);
        folder.setName(newName);
        return folderRepository.save(folder);
    }

    public void deleteFolder(Long folderId, User user) {
        Folder folder = getOwnedFolder(folderId, user);
        folderRepository.delete(folder);
    }

    public Folder getOwnedFolder(Long folderId, User user) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return folder;
    }
}
