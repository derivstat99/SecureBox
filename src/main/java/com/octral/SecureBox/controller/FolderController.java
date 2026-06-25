package com.octral.SecureBox.controller;

import com.octral.SecureBox.dto.FolderResponse;
import com.octral.SecureBox.model.Folder;
import com.octral.SecureBox.security.SecurityUtils;
import com.octral.SecureBox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @RequestParam String name,
            @RequestParam(required = false) Long parentId
    ) {
        Folder folder = folderService.addFolder(name, securityUtils.getCurrentUser(), parentId);
        return ResponseEntity.ok(FolderResponse.fromFlat(folder));
    }

    @GetMapping
    public ResponseEntity<List<FolderResponse>> getFolderHierarchy() {
        return ResponseEntity.ok(folderService.getFolderHierarchy(securityUtils.getCurrentUser()));
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<FolderResponse> renameFolder(
            @PathVariable Long folderId,
            @RequestParam String name
    ) {
        Folder folder = folderService.renameFolder(folderId, name, securityUtils.getCurrentUser());
        return ResponseEntity.ok(FolderResponse.fromFlat(folder));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId, securityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
