package com.example.fileupload.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_metadata")
@Data
public class FileMetaData {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;  // S3 object key

    @Column(name = "original_name", nullable = false)
    private String originalName;  // Original file name

    @Column(name = "content_type", nullable = false)
    private String contentType;  // MIME type of the file

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;  // Size of the file in bytes

    @Column(name = "status", nullable = false)
    private String status;  // Status of the upload (PENDING, UPLOADING, COMPLETED, FAILED)

    @Column(name = "s3_upload_id")
    private String s3UploadId;  // Upload ID for multipart uploads

    @Column(name = "file_path", nullable = false)
    private String filePath;  // S3 object path (file key)

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;


    public FileMetaData(UUID id, String bucket, String objectKey, String originalName, String contentType, long sizeBytes, String status, String s3UploadId, String filePath, OffsetDateTime createdAt) {
        this.id = id;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.originalName = originalName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.status = status;
        this.s3UploadId = s3UploadId;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public FileMetaData() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getS3UploadId() {
        return s3UploadId;
    }

    public void setS3UploadId(String s3UploadId) {
        this.s3UploadId = s3UploadId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
