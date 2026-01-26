package com.example.fileupload.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class InitMultipartUploadResponse {

    private UUID fileId;
    private String uploadId;  // The upload ID for the multipart upload
    private String filePath;  // The S3 file path (key) for the file
    //private Map<Integer, String> presignedPartUrls;  // Presigned URLs for each part (part number -> presigned URL)
    private int partCount;
    private int partSizeBytes;

    // Constructor

    public InitMultipartUploadResponse(UUID fileId, String uploadId, String filePath, int partCount, int partSizeBytes) {
        this.fileId = fileId;
        this.uploadId = uploadId;
        this.filePath = filePath;
        this.partCount = partCount;
        this.partSizeBytes = partSizeBytes;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public int getPartSizeBytes() {
        return partSizeBytes;
    }

    public void setPartSizeBytes(int partSizeBytes) {
        this.partSizeBytes = partSizeBytes;
    }
}
