package com.octral.SecureBox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryResponse {
    private Long fileId;
    private String fileName;
    private String summary;
    private boolean truncated;
}
