package com.example.fileupload.dto;

import java.util.List;
import java.util.UUID;

public class CompleteUploadRequest {
    private UUID fileId;

    // Only for multipart uploads
    private String uploadId;
    private List<CompletedPartDto> completedParts;

    public CompleteUploadRequest() {}

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public List<CompletedPartDto> getCompletedParts() {
        return completedParts;
    }

    public void setCompletedParts(List<CompletedPartDto> completedParts) {
        this.completedParts = completedParts;
    }
}
