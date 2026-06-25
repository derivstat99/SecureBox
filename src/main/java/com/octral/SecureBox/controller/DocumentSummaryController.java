package com.octral.SecureBox.controller;

import com.octral.SecureBox.dto.SummaryResponse;
import com.octral.SecureBox.security.SecurityUtils;
import com.octral.SecureBox.service.DocumentSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class DocumentSummaryController {

    @Autowired
    private DocumentSummaryService documentSummaryService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/{fileId}/summarize")
    public ResponseEntity<SummaryResponse> summarizeFile(@PathVariable Long fileId) throws IOException {
        SummaryResponse response = documentSummaryService.summarizePdf(
                fileId,
                securityUtils.getCurrentUser()
        );
        return ResponseEntity.ok(response);
    }
}
