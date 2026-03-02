package com.smartcloud.dto;

import java.io.File;
import java.time.LocalDateTime;
import com.smartcloud.entity.FileObject;

public class FileResponseDto {
    private Long id;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long size;
    private String bucket;
    private String objectKey;
    private LocalDateTime uploadedAt;

    public FileResponseDto() {
    }

    public FileResponseDto(Long id, String originalFilename, String storedFilename, String contentType, Long size,
            String bucket, String objectKey, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.size = size;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.uploadedAt = uploadedAt;
    }

    public static FileResponseDto fromEntity(FileObject fileObject) {
        return new FileResponseDto(
                fileObject.getId(),
                fileObject.getOriginalFilename(),
                fileObject.getStoredFilename(),
                fileObject.getContentType(),
                fileObject.getSize(),
                fileObject.getBucket(),
                fileObject.getObjectKey(),
                fileObject.getUploadedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
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

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

}
