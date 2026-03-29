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
    private Long finalSize;

    private FileStatus status;

    private boolean optimized;
    private boolean downloadable;

    private Double compressionRatio;

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
            Long finalSize,
            FileStatus status,
            boolean optimized,
            boolean downloadable,
            Double compressionRatio,
            LocalDateTime uploadedAt,
            String downloadUrl) {

        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeOriginal = sizeOriginal;
        this.sizeOptimized = sizeOptimized;
        this.finalSize = finalSize;
        this.status = status;
        this.optimized = optimized;
        this.downloadable = downloadable;
        this.compressionRatio = compressionRatio;
        this.uploadedAt = uploadedAt;
        this.downloadUrl = downloadUrl;
    }

    public static FileResponseDto fromEntity(FileObject file) {

        Long finalSize = file.getSizeOptimized() != null
                ? file.getSizeOptimized()
                : file.getSizeOriginal();

        boolean optimized = file.getStatus() == FileStatus.OPTIMIZED;

        boolean downloadable =
                file.getStatus() == FileStatus.OPTIMIZED ||
                file.getStatus() == FileStatus.COMPLETED;

        Double compressionRatio = null;

        if (file.getSizeOptimized() != null && file.getSizeOriginal() != null && file.getSizeOriginal() > 0) {
            compressionRatio = 100.0 * (1 - ((double) file.getSizeOptimized() / file.getSizeOriginal()));
        }

        String downloadUrl = downloadable
                ? "/api/files/" + file.getId() + "/download"
                : null;

        return new FileResponseDto(
                file.getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSizeOriginal(),
                file.getSizeOptimized(),
                finalSize,
                file.getStatus(),
                optimized,
                downloadable,
                compressionRatio,
                file.getUploadedAt(),
                downloadUrl
        );
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

    public Long getFinalSize() {
        return finalSize;
    }

    public FileStatus getStatus() {
        return status;
    }

    public boolean isOptimized() {
        return optimized;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public Double getCompressionRatio() {
        return compressionRatio;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
