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
        return new FileListItemDto(
                file.getId(),
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                file.getUploadedAt(),
                "/api/files/" + file.getId() + "/download"
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}