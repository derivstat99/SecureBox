package com.octral.SecureBox.dto;

import com.octral.SecureBox.model.Folder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FolderResponse {
    private Long id;
    private String name;
    private Long parentId;
    private List<FolderResponse> subFolders;

    public static FolderResponse from(Folder folder) {
        List<FolderResponse> children = folder.getSubFolders().stream()
                .map(FolderResponse::from)
                .collect(Collectors.toList());
        Long parentId = folder.getParent() != null ? folder.getParent().getId() : null;
        return new FolderResponse(folder.getId(), folder.getName(), parentId, children);
    }

    public static FolderResponse fromFlat(Folder folder) {
        Long parentId = folder.getParent() != null ? folder.getParent().getId() : null;
        return new FolderResponse(folder.getId(), folder.getName(), parentId, List.of());
    }
}
