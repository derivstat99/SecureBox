package com.octral.SecureBox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StorageUsageResponse {
    private long totalBytesUsed;
    private long totalFiles;
    private long totalBytesLimit;
    private double usagePercent;
}
