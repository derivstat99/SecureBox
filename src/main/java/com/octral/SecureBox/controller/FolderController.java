package com.octral.SecureBox.controller;

import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.repository.FolderRepository;
import com.octral.SecureBox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @PostMapping("/folder")
    public ResponseEntity<Folder> addFolder(@RequestParam String name,@RequestParam Long userId, @RequestParam(required = false) Long parentId){
        return ResponseEntity.ok(folderService.addFolder(name,userId,parentId));
    }
}
