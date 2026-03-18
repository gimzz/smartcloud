package com.smartcloud.dto;

import java.time.LocalDateTime;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.FileStatus;

public class FileResponseDto {

    private Long id;
    private String originalFilename;
    private String contentType;

    private Long sizeOriginal;
    private Long sizeOptimized;

    private FileStatus status;

    private LocalDateTime uploadedAt;

    private String downloadUrl;

    public FileResponseDto() {
    }

    public FileResponseDto(
            Long id,
            String originalFilename,
            String contentType,
            Long sizeOriginal,
            Long sizeOptimized,
            FileStatus status,
            LocalDateTime uploadedAt,
            String downloadUrl) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeOriginal = sizeOriginal;
        this.sizeOptimized = sizeOptimized;
        this.status = status;
        this.uploadedAt = uploadedAt;
        this.downloadUrl = downloadUrl;
    }

    public static FileResponseDto fromEntity(FileObject file) {

        Long finalSize = file.getSizeOptimized() != null
                ? file.getSizeOptimized()
                : file.getSizeOriginal();

        return new FileResponseDto(
                file.getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSizeOriginal(),
                file.getSizeOptimized(),
                file.getStatus(),
                file.getUploadedAt(),
                "/api/files/" + file.getId() + "/download");
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSizeOriginal() {
        return sizeOriginal;
    }

    public Long getSizeOptimized() {
        return sizeOptimized;
    }

    public FileStatus getStatus() {
        return status;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}