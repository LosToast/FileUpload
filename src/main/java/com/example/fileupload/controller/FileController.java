package com.example.fileupload.controller;

import com.example.fileupload.service.S3FileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {
    private final S3FileService fileService;

    public FileController(S3FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file) throws Exception {
        UUID uuid = fileService.uploadAndSave(file);
        return Map.of("message", "uploaded", "key", uuid);
    }
}
