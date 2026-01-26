package com.example.fileupload.dto;

public class FileUploadRequest {
    private String fileName;
    private long fileSize;
    private String contentType;

    public FileUploadRequest() {}

    public FileUploadRequest(String fileName, long fileSize, String contentType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
