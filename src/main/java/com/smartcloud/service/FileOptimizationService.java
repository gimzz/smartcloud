package com.smartcloud.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.FileStatus;
import org.springframework.transaction.annotation.Transactional;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import net.coobird.thumbnailator.Thumbnails;

@Service
public class FileOptimizationService {

    private final MinioClient minioClient;
    private final FileObjectService fileObjectService;

    @Value("${minio.bucket}")
    private String bucket;

    public FileOptimizationService(
            MinioClient minioClient,
            FileObjectService fileObjectService
    ) {
        this.minioClient = minioClient;
        this.fileObjectService = fileObjectService;
    }

 @Transactional
public void optimize(FileObject file) throws Exception {

    System.out.println("OPTIMIZANDO ARCHIVO: " + file.getOriginalFilename());

    if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
        return;
    }

    InputStream originalStream = minioClient.getObject(
            GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(file.getObjectKeyOriginal())
                    .build()
    );

    ByteArrayOutputStream optimizedOutput = optimizeImage(originalStream);

    byte[] optimizedBytes = optimizedOutput.toByteArray();
    long optimizedSize = optimizedBytes.length;

    InputStream optimizedStream = new ByteArrayInputStream(optimizedBytes);

    String optimizedKey = file.getObjectKeyOriginal() + "-optimized.jpg";

    minioClient.putObject(
            PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(optimizedKey)
                    .stream(optimizedStream, optimizedSize, -1)
                    .contentType("image/jpeg")
                    .build()
    );

    file.setObjectKeyOptimized(optimizedKey);
    file.setSizeOptimized(optimizedSize);
    file.setStatus(FileStatus.OPTIMIZED);

    fileObjectService.save(file);

    System.out.println("BD ACTUALIZADA");
}

   private ByteArrayOutputStream optimizeImage(InputStream original) throws Exception {

    BufferedImage image = ImageIO.read(original);

    if (image == null) {
        throw new RuntimeException("ImageIO could not read image");
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();

    Thumbnails.of(image)
            .scale(0.8)
            .outputQuality(0.6)
            .outputFormat("jpg")
            .toOutputStream(output);

    return output;
}
}