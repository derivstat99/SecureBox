package com.octral.SecureBox.service;

import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FileRepository;
import com.octral.SecureBox.repository.FolderRepository;
import com.octral.SecureBox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    public FileMetaData uploadFile(MultipartFile file, Long userId, Long folderId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        if (file.getSize() > 5 * 1024 * 1024) throw new RuntimeException("File too large");

        String fileUrl = cloudinaryService.upload(file);

        FileMetaData meta = new FileMetaData();
        meta.setFileName(file.getOriginalFilename());
        meta.setFileSize(file.getSize());
        meta.setFileType(file.getContentType());
        meta.setFilePath(fileUrl);
        meta.setUser(user);

        if (folderId != null) {
            Folder folder = folderRepository.findById(folderId).orElseThrow();
            meta.setFolder(folder);
            folder.getFiles().add(meta);
        }

        return fileRepository.save(meta);
    }

    public FileMetaData getFileMetaData(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow();
    }
}