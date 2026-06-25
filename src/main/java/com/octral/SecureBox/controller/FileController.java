package com.octral.SecureBox.controller;

import com.octral.SecureBox.dto.FileResponse;
import com.octral.SecureBox.dto.StorageUsageResponse;
import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.security.SecurityUtils;
import com.octral.SecureBox.service.CloudinaryService;
import com.octral.SecureBox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(
            @RequestPart MultipartFile file,
            @RequestParam(required = false) Long folderId
    ) throws IOException {
        return ResponseEntity.ok(
                fileService.uploadFile(file, securityUtils.getCurrentUser(), folderId)
        );
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllUserFiles(securityUtils.getCurrentUser()));
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<FileResponse>> getFilesInFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(
                fileService.getFilesInFolder(folderId, securityUtils.getCurrentUser())
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileResponse>> searchFiles(@RequestParam String q) {
        return ResponseEntity.ok(
                fileService.searchByFileName(securityUtils.getCurrentUser(), q)
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FileResponse>> getRecentUploads(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                fileService.getRecentUploads(securityUtils.getCurrentUser(), limit)
        );
    }

    @GetMapping("/storage")
    public ResponseEntity<StorageUsageResponse> getStorageUsage() {
        return ResponseEntity.ok(fileService.getStorageUsage(securityUtils.getCurrentUser()));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FileResponse>> getFavoriteFiles() {
        return ResponseEntity.ok(fileService.getFavoriteFiles(securityUtils.getCurrentUser()));
    }

    @GetMapping("/trash")
    public ResponseEntity<List<FileResponse>> getTrashFiles() {
        return ResponseEntity.ok(fileService.getTrashFiles(securityUtils.getCurrentUser()));
    }

    @PutMapping("/{fileId}/favorite")
    public ResponseEntity<FileResponse> toggleFavorite(@PathVariable Long fileId) {
        return ResponseEntity.ok(
                fileService.toggleFavorite(fileId, securityUtils.getCurrentUser())
        );
    }

    @PostMapping("/{fileId}/restore")
    public ResponseEntity<FileResponse> restoreFile(@PathVariable Long fileId) {
        return ResponseEntity.ok(
                fileService.restoreFile(fileId, securityUtils.getCurrentUser())
        );
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) throws IOException {
        FileMetaData meta = fileService.getOwnedFile(fileId, securityUtils.getCurrentUser());
        byte[] data = cloudinaryService.download(meta.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getFileType()))
                .body(data);
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<FileResponse> renameFile(
            @PathVariable Long fileId,
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                fileService.renameFile(fileId, name, securityUtils.getCurrentUser())
        );
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> moveToTrash(@PathVariable Long fileId) {
        fileService.moveToTrash(fileId, securityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{fileId}/permanent")
    public ResponseEntity<Void> permanentDeleteFile(@PathVariable Long fileId) throws IOException {
        fileService.permanentDeleteFile(fileId, securityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
