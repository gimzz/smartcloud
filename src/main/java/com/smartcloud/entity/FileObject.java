package com.smartcloud.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "file_object",
       indexes = {
           @Index(name = "idx_file_status", columnList = "status")
       }
)

public class FileObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "object_key_original", nullable = false, length = 500)
    private String objectKeyOriginal;

    @Column(name = "object_key_optimized", length = 500)
    private String objectKeyOptimized;

    @Column(name = "size_original", nullable = false)
    private Long sizeOriginal;

    @Column(name = "size_optimized")
    private Long sizeOptimized;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FileStatus status;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "bucket", nullable = false, length = 100)
    private String bucket;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    protected FileObject() {
    }

    public FileObject(
            String originalFilename,
            String contentType,
            Long sizeOriginal,
            String bucket,
            String objectKeyOriginal,
            User owner) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeOriginal = sizeOriginal;
        this.bucket = bucket;
        this.objectKeyOriginal = objectKeyOriginal;
        this.owner = owner;
        this.status = FileStatus.UPLOADED;
        this.uploadedAt = LocalDateTime.now();
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


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

 

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getObjectKeyOriginal() {
        return objectKeyOriginal;
    }

    public void setObjectKeyOriginal(String objectKeyOriginal) {
        this.objectKeyOriginal = objectKeyOriginal;
    }

    public String getObjectKeyOptimized() {
        return objectKeyOptimized;
    }

    public void setObjectKeyOptimized(String objectKeyOptimized) {
        this.objectKeyOptimized = objectKeyOptimized;
    }

    public Long getSizeOriginal() {
        return sizeOriginal;
    }

    public void setSizeOriginal(Long sizeOriginal) {
        this.sizeOriginal = sizeOriginal;
    }

    public Long getSizeOptimized() {
        return sizeOptimized;
    }

    public void setSizeOptimized(Long sizeOptimized) {
        this.sizeOptimized = sizeOptimized;
    }
    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }
}
