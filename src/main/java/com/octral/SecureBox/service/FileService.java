package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.FileResponse;
import com.octral.SecureBox.dto.StorageUsageResponse;
import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long USER_STORAGE_LIMIT = 100 * 1024 * 1024;

    @Autowired
    private FileRepository fileRepository;

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
        meta.setUploadedAt(LocalDateTime.now());

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
        return fileRepository.findByUserIdAndDeletedFalse(user.getId()).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public List<FileResponse> getFilesInFolder(Long folderId, User user) {
        folderService.getOwnedFolder(folderId, user);
        return fileRepository.findByFolderIdAndDeletedFalse(folderId).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public FileResponse renameFile(Long fileId, String newName, User user) {
        FileMetaData meta = getActiveFile(fileId, user);
        meta.setFileName(newName);
        return FileResponse.from(fileRepository.save(meta));
    }

    public void moveToTrash(Long fileId, User user) {
        FileMetaData meta = getActiveFile(fileId, user);
        meta.setDeleted(true);
        meta.setDeletedAt(LocalDateTime.now());
        fileRepository.save(meta);
    }

    public List<FileResponse> getTrashFiles(User user) {
        return fileRepository.findByUserIdAndDeletedTrue(user.getId()).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public FileResponse restoreFile(Long fileId, User user) {
        FileMetaData meta = getOwnedFile(fileId, user);
        if (!meta.isDeleted()) {
            throw new RuntimeException("File is not in trash");
        }
        meta.setDeleted(false);
        meta.setDeletedAt(null);
        return FileResponse.from(fileRepository.save(meta));
    }

    public void permanentDeleteFile(Long fileId, User user) throws IOException {
        FileMetaData meta = getOwnedFile(fileId, user);
        if (!meta.isDeleted()) {
            throw new RuntimeException("Move file to trash before permanent delete");
        }
        if (meta.getCloudinaryPublicId() != null) {
            cloudinaryService.delete(meta.getCloudinaryPublicId());
        }
        fileRepository.delete(meta);
    }

    public FileResponse toggleFavorite(Long fileId, User user) {
        FileMetaData meta = getActiveFile(fileId, user);
        meta.setFavorite(!meta.isFavorite());
        return FileResponse.from(fileRepository.save(meta));
    }

    public List<FileResponse> getFavoriteFiles(User user) {
        return fileRepository.findByUserIdAndFavoriteTrueAndDeletedFalse(user.getId()).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public List<FileResponse> searchByFileName(User user, String query) {
        return fileRepository.findByUserIdAndFileNameContainingIgnoreCaseAndDeletedFalse(user.getId(), query).stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    public StorageUsageResponse getStorageUsage(User user) {
        long totalBytes = fileRepository.sumFileSizeByUserIdAndDeletedFalse(user.getId());
        long totalFiles = fileRepository.countByUserIdAndDeletedFalse(user.getId());
        double usagePercent = USER_STORAGE_LIMIT == 0 ? 0
                : (totalBytes * 100.0) / USER_STORAGE_LIMIT;
        return new StorageUsageResponse(totalBytes, totalFiles, USER_STORAGE_LIMIT, usagePercent);
    }

    public List<FileResponse> getRecentUploads(User user, int limit) {
        return fileRepository.findByUserIdAndDeletedFalseOrderByUploadedAtDesc(user.getId()).stream()
                .limit(limit)
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }

    private FileMetaData getActiveFile(Long fileId, User user) {
        FileMetaData meta = getOwnedFile(fileId, user);
        if (meta.isDeleted()) {
            throw new RuntimeException("File is in trash");
        }
        return meta;
    }
}
