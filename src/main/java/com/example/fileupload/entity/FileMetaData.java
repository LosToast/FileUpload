package com.example.fileupload.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_metadata")
@Data
public class FileMetaData {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "original_name", nullable = false)
    private String originalName;
    @Column(name = " content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;


    public FileMetaData(UUID id, String bucket, String objectKey, String originalName, String contentType, long sizeBytes, OffsetDateTime createdAt) {
        this.id = id;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.originalName = originalName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
    }

    public FileMetaData() {
    }
}
