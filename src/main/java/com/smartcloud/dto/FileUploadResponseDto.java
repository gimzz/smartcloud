package com.smartcloud.dto;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.FileStatus;

public class FileUploadResponseDto {

    private Long id;
    private String originalFilename;
    private FileStatus status;
    private String statusUrl; 

    public FileUploadResponseDto() {
    }

    public FileUploadResponseDto(Long id, String originalFilename, FileStatus status) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.status = status;
        this.statusUrl = "/api/files/" + id; 
    }

    public static FileUploadResponseDto fromEntity(FileObject file) {
        return new FileUploadResponseDto(
                file.getId(),
                file.getOriginalFilename(),
                file.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public FileStatus getStatus() {
        return status;
    }

    public String getStatusUrl() {
        return statusUrl;
    }
}