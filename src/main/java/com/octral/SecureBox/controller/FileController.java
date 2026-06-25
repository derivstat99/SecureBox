package com.octral.SecureBox.controller;

import com.octral.SecureBox.dto.FileResponse;
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
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) throws IOException {
        fileService.deleteFile(fileId, securityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
