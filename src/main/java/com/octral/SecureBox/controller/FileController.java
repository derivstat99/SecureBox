package com.octral.SecureBox.controller;

import com.octral.SecureBox.service.FileService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file, @RequestParam Long userId, @RequestParam(required = false) Long folderId){
        return ResponseEntity.ok(fileService.uploadFile(file,userId,folderId));
    }

}
