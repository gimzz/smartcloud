package com.smartcloud.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.User;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    private final MinioClient minioClient;
    private final String bucket;
    private final FileObjectService fileObjectService;

    public StorageService(
            MinioClient minioClient,
            FileObjectService fileObjectService,
            @Value("${minio.bucket}") String bucket
    ) {
        this.minioClient = minioClient;
        this.fileObjectService = fileObjectService;
        this.bucket = bucket;
    }

    public FileObject upload(
            MultipartFile file,
            User user
    ) throws Exception {

        ensureBucketExists();

        String objectKey = buildObjectKey(user.getId(), file.getOriginalFilename());

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        FileObject metadata = new FileObject(
                file.getOriginalFilename(),
                objectKey,
                file.getContentType(),
                file.getSize(),
                bucket,
                objectKey,
                user
        );

        return fileObjectService.save(metadata);
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }

    private String buildObjectKey(Long userId, String filename) {
        String cleanName = filename == null ? "file" : filename.replaceAll("\\s+", "_");
        return "users/" + (userId != null ? userId : "anonymous") + "/" + UUID.randomUUID() + "-" + cleanName;
    }

    public java.io.InputStream download(FileObject file) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(file.getBucket())
                        .object(file.getObjectKey())
                        .build()
        );
    }

    public void delete(FileObject file) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(file.getBucket())
                        .object(file.getObjectKey())
                        .build()
        );
    }
}