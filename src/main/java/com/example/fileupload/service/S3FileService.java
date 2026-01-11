package com.example.fileupload.service;

import com.example.fileupload.entity.FileMetaData;
import com.example.fileupload.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class S3FileService {
    private final S3Client s3;
    private final String bucket;
    private final FileMetadataRepository repo;


    public S3FileService(S3Client s3, @Value("${app.aws.bucket}") String bucket , FileMetadataRepository repo) {
        this.s3 = s3;
        this.bucket = bucket;
        this.repo = repo;
    }

    public UUID  uploadAndSave(MultipartFile file) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");

        String safeName = (file.getOriginalFilename() == null) ? "file" :
                file.getOriginalFilename().replaceAll("[\\\\/]+", "_");

        UUID id = UUID.randomUUID();

        String key = "uploads/" + id + "/" + safeName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream in = file.getInputStream()) {
            s3.putObject(req, RequestBody.fromInputStream(in, file.getSize()));
        }
        repo.save(new FileMetaData(
                id, bucket, key, safeName, file.getContentType(), file.getSize(),
                OffsetDateTime.now()
        ));

        return id; // store this in Postgres later as storage_key
    }
}
