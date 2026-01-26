package com.example.fileupload.dto;

import java.util.UUID;

public class SingleUploadInitResponse {
    private UUID fileId;
    private String presignedUrl;

    public SingleUploadInitResponse() {}

    public SingleUploadInitResponse(UUID fileId, String presignedUrl) {
        this.fileId = fileId;
        this.presignedUrl = presignedUrl;
    }

    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }

    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String presignedUrl) { this.presignedUrl = presignedUrl; }
}
