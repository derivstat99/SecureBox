package com.octral.SecureBox.controller;

import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.service.CloudinaryService;
import com.octral.SecureBox.service.FileService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private CloudinaryService cloudinaryService;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file, @RequestParam Long userId, @RequestParam(required = false) Long folderId){
        return ResponseEntity.ok(fileService.uploadFile(file,userId,folderId));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) throws IOException {
        FileMetaData meta= fileService.getFileMetaData(fileId);
        byte[] data=cloudinaryService.download(meta.getFilePath());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\\\"\" + meta.getFileName() + \"\\\"\"")
                .contentType(MediaType.parseMediaType(meta.getFileType()))
                .body(data);

    }

}
