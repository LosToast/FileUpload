package com.example.fileupload.service;

import com.example.fileupload.dto.CompletedPartDto;
import com.example.fileupload.dto.InitMultipartUploadResponse;
import com.example.fileupload.dto.SingleUploadInitResponse;
import com.example.fileupload.entity.FileMetaData;
import com.example.fileupload.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class S3FileService {
    private static final int PART_SIZE_BYTES = 64 * 1024 * 1024; // 64MB
    private static final long FIVE_GB = 5L * 1024 * 1024 * 1024;
    private final S3Presigner presigner;
    private final S3Client s3;
    private final FileMetadataRepository repo;

    @Value("${app.aws.bucket}")
    private String bucketName;

    public S3FileService(S3Presigner presigner, S3Client s3, FileMetadataRepository repo) {
        this.presigner = presigner;
        this.s3 = s3;
        this.repo = repo;
    }

    // ✅ Small file init: create DB row + return (fileId, presignedUrl)
    public SingleUploadInitResponse initSingleUpload(String fileName, long fileSize, String contentType) {

        String filePath = "uploads/" + UUID.randomUUID() + "/" + fileName;
        FileMetaData meta = new FileMetaData();
        meta.setOriginalName(fileName);
        meta.setSizeBytes(fileSize);
        meta.setStatus("PENDING");
        meta.setBucket(bucketName);
        meta.setContentType(contentType);
        meta.setFilePath(filePath);// better: "uploads/"+UUID+"/"+fileName
        meta.setObjectKey(filePath);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(meta.getFilePath())
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .putObjectRequest(putReq)
                        .signatureDuration(Duration.ofHours(2))
                        .build()
        );
        repo.save(meta);

        return new SingleUploadInitResponse(meta.getId(), presigned.url().toString());
    }
    // Generate presigned URL for single PUT upload when data is less than 5gb
    public String generatePresignedUrl(String fileName, long fileSize) {
        Duration expiration = Duration.ofHours(2);  // 2-hour expiration for single upload
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(expiration)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();  // Return the presigned URL
    }

    //This method initiates the multipart upload process for large files (> 5GB)
    public InitMultipartUploadResponse initiateMultipartUpload(String fileName, long fileSize, String contentType) {
        String filePath = "uploads/" + UUID.randomUUID() + "/" + fileName;
        FileMetaData meta = new FileMetaData();
        meta.setOriginalName(fileName);
        meta.setSizeBytes(fileSize);
        meta.setStatus("UPLOADING");
        meta.setBucket(bucketName);
        meta.setContentType(contentType);
        meta.setFilePath(filePath);
        meta.setObjectKey(filePath);

        CreateMultipartUploadResponse createRes = s3.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(meta.getFilePath())
                        .contentType(contentType != null ? contentType : "application/octet-stream")
                        .build()
        );

        String uploadId = createRes.uploadId();
        meta.setS3UploadId(uploadId);
        repo.save(meta);

        Map<Integer, String> urls = generatePresignedUrls(uploadId, meta.getFilePath(), fileSize);

        return new InitMultipartUploadResponse(meta.getId(), uploadId, meta.getFilePath(), urls , PART_SIZE_BYTES);

    }

    // Generate presigned URLs for multipart parts
    private Map<Integer, String> generatePresignedUrls(String uploadId, String fileName, long fileSize) {
        Map<Integer, String> presignedUrls = new HashMap<>();
        int partCount = (int) Math.ceil((double) fileSize / (64L * 1024 * 1024));  // 64MB parts
        for (int i = 1; i <= partCount; i++) {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .partNumber(i)
                    .build();

            UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest)
                    .signatureDuration(Duration.ofHours(2))  // Presigned URL valid for 2 hours
                    .build();

            PresignedUploadPartRequest presignedPartRequest = presigner.presignUploadPart(uploadPartPresignRequest);
            presignedUrls.put(i, presignedPartRequest.url().toString());
        }
        return presignedUrls;
    }

    // Complete single upload
    public void completeSingleUpload(UUID fileId) {
        FileMetaData meta = repo.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        meta.setStatus("COMPLETED");
        repo.save(meta);  // Mark the file as completed
    }

    // Complete multipart upload
    public void completeMultipartUpload(UUID fileId, String uploadId, List<CompletedPartDto> completedParts) {

        if (fileId == null) throw new IllegalArgumentException("fileId is required");
        if (uploadId == null || uploadId.isBlank()) throw new IllegalArgumentException("uploadId is required");
        if (completedParts == null || completedParts.isEmpty()) throw new IllegalArgumentException("completedParts is required");

        FileMetaData meta = repo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // ✅ Optional but recommended: ensure uploadId matches DB record
        if (meta.getS3UploadId() != null && !meta.getS3UploadId().equals(uploadId)) {
            throw new IllegalArgumentException("uploadId does not match this file record");
        }

        // ✅ Validate + de-duplicate by partNumber (keep first occurrence)
        Map<Integer, String> partMap = new HashMap<>();
        for (CompletedPartDto p : completedParts) {
            if (p == null) throw new IllegalArgumentException("completedParts contains null");
            if (p.getPartNumber() <= 0) throw new IllegalArgumentException("Invalid partNumber: " + p.getPartNumber());
            if (p.getETag() == null || p.getETag().isBlank()) throw new IllegalArgumentException("Missing ETag for part " + p.getPartNumber());

            // keep first; or you can overwrite — but duplicates should not happen
            partMap.putIfAbsent(p.getPartNumber(), p.getETag());
        }

        // ✅ Build parts sorted by partNumber (S3 requires this)
        List<CompletedPart> parts = partMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> CompletedPart.builder()
                        .partNumber(e.getKey())
                        .eTag(e.getValue())
                        .build())
                .toList();

        try {
            CompletedMultipartUpload cmu = CompletedMultipartUpload.builder()
                    .parts(parts)
                    .build();

            CompleteMultipartUploadRequest cReq = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(meta.getFilePath())
                    .uploadId(uploadId)
                    .multipartUpload(cmu)
                    .build();

            s3.completeMultipartUpload(cReq);

            meta.setStatus("COMPLETED");
            repo.save(meta);

        } catch (Exception ex) {
            // ✅ Recommended: mark failed so DB doesn't stay "UPLOADING" forever
            meta.setStatus("FAILED");
            repo.save(meta);
            throw ex;
        }
    }

}
