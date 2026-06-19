package com.octral.SecureBox.service;

import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FolderRepository;
import com.octral.SecureBox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    public Folder addFolder(String name,Long userId, Long parentId){
        User user=userRepository.findById(userId).orElseThrow();

        Folder folder=new Folder();
        folder.setName(name);
        folder.setUser(user);

        if(parentId!=null){
            Folder parent=folderRepository.findById(parentId).orElseThrow();
            folder.setParent(parent);
        }

        return folderRepository.save(folder);

    }
}
