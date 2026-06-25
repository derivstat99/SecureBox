package com.octral.SecureBox.dto;

import com.octral.SecureBox.model.FileMetaData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private Long folderId;
    private boolean favorite;
    private LocalDateTime uploadedAt;

    public static FileResponse from(FileMetaData meta) {
        Long folderId = meta.getFolder() != null ? meta.getFolder().getId() : null;
        return new FileResponse(
                meta.getId(),
                meta.getFileName(),
                meta.getFilePath(),
                meta.getFileSize(),
                meta.getFileType(),
                folderId,
                meta.isFavorite(),
                meta.getUploadedAt()
        );
    }
}
