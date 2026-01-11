package com.example.fileupload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
public class S3FileService {
    private final S3Client s3;
    private final String bucket;

    public S3FileService(S3Client s3, @Value("${app.aws.bucket}") String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");

        String safeName = (file.getOriginalFilename() == null) ? "file" :
                file.getOriginalFilename().replaceAll("[\\\\/]+", "_");

        String key = "uploads/" + UUID.randomUUID() + "/" + safeName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream in = file.getInputStream()) {
            s3.putObject(req, RequestBody.fromInputStream(in, file.getSize()));
        }

        return key; // store this in Postgres later as storage_key
    }
}
