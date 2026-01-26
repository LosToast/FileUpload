package com.example.fileupload.dto;

import lombok.Data;

@Data
public class CompletedPartDto {
    private int partNumber;  // The part number (1, 2, 3, ...)
    private String ETag;     // The ETag returned by S3 for the uploaded part

    // Constructor
    public CompletedPartDto(int partNumber, String eTag) {
        this.partNumber = partNumber;
        this.ETag = eTag;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }
}
