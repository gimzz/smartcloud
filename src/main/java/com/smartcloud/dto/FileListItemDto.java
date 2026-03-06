package com.smartcloud.dto;

import java.time.LocalDateTime;

import com.smartcloud.entity.FileObject;

public class FileListItemDto {

    private Long id;
    private String filename;
    private Long size;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String downloadUrl;

    public FileListItemDto() {}

    public FileListItemDto(
            Long id,
            String filename,
            Long size,
            String contentType,
            LocalDateTime uploadedAt,
            String downloadUrl
    ) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.contentType = contentType;
        this.uploadedAt = uploadedAt;
        this.downloadUrl = downloadUrl;
    }

    public static FileListItemDto fromEntity(FileObject file) {

        Long size = file.getSizeOptimized() != null
                ? file.getSizeOptimized()
                : file.getSizeOriginal();

        return new FileListItemDto(
                file.getId(),
                file.getOriginalFilename(),
                size,
                file.getContentType(),
                file.getUploadedAt(),
                "/api/files/" + file.getId() + "/download"
        );
    }

    public Long getId() { return id; }

    public String getFilename() { return filename; }

    public Long getSize() { return size; }

    public String getContentType() { return contentType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public String getDownloadUrl() { return downloadUrl; }
}