package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.FileResponse;
import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FileRepository;
import com.octral.SecureBox.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private CloudinaryService cloudinaryService;

    public FileResponse uploadFile(MultipartFile file, User user, Long folderId) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File too large (max 5MB)");
        }

        Map<String, String> uploadResult = cloudinaryService.upload(file);

        FileMetaData meta = new FileMetaData();
        meta.setFileName(file.getOriginalFilename());
        meta.setFileSize(file.getSize());
        meta.setFileType(file.getContentType());
        meta.setFilePath(uploadResult.get("url"));
        meta.setCloudinaryPublicId(uploadResult.get("publicId"));
        meta.setUser(user);

        if (folderId != null) {
            Folder folder = folderService.getOwnedFolder(folderId, user);
            meta.setFolder(folder);
        }

        return FileResponse.from(fileRepository.save(meta));
    }

    public FileMetaData getOwnedFile(Long fileId, User user) {
        FileMetaData meta = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!meta.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return meta;
    }

    public List<FileResponse> getAllUserFiles(User user) {
        return fileRepository.findByUserId(user.getId()).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public List<FileResponse> getFilesInFolder(Long folderId, User user) {
        folderService.getOwnedFolder(folderId, user);
        return fileRepository.findByFolderId(folderId).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public FileResponse renameFile(Long fileId, String newName, User user) {
        FileMetaData meta = getOwnedFile(fileId, user);
        meta.setFileName(newName);
        return FileResponse.from(fileRepository.save(meta));
    }

    public void deleteFile(Long fileId, User user) throws IOException {
        FileMetaData meta = getOwnedFile(fileId, user);
        if (meta.getCloudinaryPublicId() != null) {
            cloudinaryService.delete(meta.getCloudinaryPublicId());
        }
        fileRepository.delete(meta);
    }
}
