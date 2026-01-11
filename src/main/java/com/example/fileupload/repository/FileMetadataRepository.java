package com.example.fileupload.repository;

import com.example.fileupload.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetaData, UUID> {
}
