package com.example.fileupload.controller;

import com.example.fileupload.dto.CompleteUploadRequest;
import com.example.fileupload.dto.FileUploadRequest;
import com.example.fileupload.dto.InitMultipartUploadResponse;
import com.example.fileupload.service.S3FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {
    private static final long MULTIPART_THRESHOLD_BYTES = 200L * 1024 * 1024; // 200MB
    private final S3FileService s3FileService;

    public FileController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    // Endpoint to initiate upload (small and large files)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestBody FileUploadRequest request) {
        long fileSize = request.getFileSize();

        if (fileSize <= MULTIPART_THRESHOLD_BYTES) {
            return ResponseEntity.ok(
                    s3FileService.initSingleUpload(request.getFileName(), request.getFileSize(), request.getContentType())
            );
        }
        return ResponseEntity.ok(
                s3FileService.initiateMultipartUpload(request.getFileName(), request.getFileSize(), request.getContentType())
        );
    }

    // Endpoint to complete upload (for both single PUT and multipart)
    @PostMapping("/complete-upload")
    public ResponseEntity<Void> completeUpload(@RequestBody CompleteUploadRequest request) {
        if (request.getFileId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Single PUT completion
        if (request.getUploadId() == null) {
            s3FileService.completeSingleUpload(request.getFileId());
            return ResponseEntity.ok().build();
        }

        // Multipart completion
        if (request.getCompletedParts() == null || request.getCompletedParts().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        s3FileService.completeMultipartUpload(
                request.getFileId(),
                request.getUploadId(),
                request.getCompletedParts()
        );

        return ResponseEntity.ok().build();
    }
    @GetMapping("/multipart/part-url")
    public ResponseEntity<Map<String, String>> getMultipartPartUrl(
            @RequestParam UUID fileId,
            @RequestParam String uploadId,
            @RequestParam int partNumber
    ) {
        String url = s3FileService.presignUploadPartUrl(fileId, uploadId, partNumber);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
