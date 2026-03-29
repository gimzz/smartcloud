package com.smartcloud.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.FileStatus;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;

@Service
public class FileOptimizationService {

    private final MinioClient minioClient;
    private final FileObjectService fileObjectService;

    @Value("${minio.bucket}")
    private String bucket;

    public FileOptimizationService(
            MinioClient minioClient,
            FileObjectService fileObjectService) {
        this.minioClient = minioClient;
        this.fileObjectService = fileObjectService;
    }

    @Transactional
    public void optimize(FileObject file) throws Exception {

        System.out.println("OPTIMIZANDO ARCHIVO: " + file.getOriginalFilename());

        file.setStatus(FileStatus.PROCESSING);
        fileObjectService.save(file);

        String type = file.getContentType();
        if (type == null)
            return;

        if (type.startsWith("image")) {
            optimizeImageFile(file);
        } else if (type.equals("application/pdf")) {
            optimizePdf(file);
        }
    }

    private void optimizeImageFile(FileObject file) throws Exception {

        InputStream originalStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(file.getObjectKeyOriginal())
                        .build());

        ByteArrayOutputStream optimizedOutput = optimizeImage(originalStream);

        byte[] optimizedBytes = optimizedOutput.toByteArray();
        long optimizedSize = optimizedBytes.length;
        long originalSize = file.getSizeOriginal();

        if (optimizedSize >= originalSize) {
            System.out.println(" No se optimizó (es igual o mayor). Se mantiene original.");
            file.setStatus(FileStatus.COMPLETED);
            fileObjectService.save(file);
            return;
        }

        InputStream optimizedStream = new ByteArrayInputStream(optimizedBytes);

        String optimizedKey = file.getObjectKeyOriginal() + "-optimized.jpg";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(optimizedKey)
                        .stream(optimizedStream, optimizedSize, -1)
                        .contentType("image/jpeg")
                        .build());

        file.setObjectKeyOptimized(optimizedKey);
        file.setSizeOptimized(optimizedSize);
        file.setStatus(FileStatus.OPTIMIZED);

        fileObjectService.save(file);

        System.out.println("IMAGEN OPTIMIZADA Y BD ACTUALIZADA");
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

    private void optimizePdf(FileObject file) throws Exception {

        InputStream originalStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(file.getObjectKeyOriginal())
                        .build());

        ByteArrayOutputStream optimizedOutput = compressPdf(originalStream);

        byte[] optimizedBytes = optimizedOutput.toByteArray();
        long optimizedSize = optimizedBytes.length;
        long originalSize = file.getSizeOriginal();

        System.out.println("PDF original: " + originalSize);
        System.out.println("PDF optimized: " + optimizedSize);

        if (optimizedSize >= originalSize) {
            System.out.println(" No se optimizó (es igual o mayor). Se mantiene original.");
            file.setStatus(FileStatus.COMPLETED);
            fileObjectService.save(file);
            return;
        }

        InputStream optimizedStream = new ByteArrayInputStream(optimizedBytes);

        String optimizedKey = file.getObjectKeyOriginal() + "-optimized.pdf";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(optimizedKey)
                        .stream(optimizedStream, optimizedSize, -1)
                        .contentType("application/pdf")
                        .build());

        file.setObjectKeyOptimized(optimizedKey);
        file.setSizeOptimized(optimizedSize);
        file.setStatus(FileStatus.OPTIMIZED);

        fileObjectService.save(file);

        System.out.println(" PDF OPTIMIZADO Y GUARDADO");
    }

    private ByteArrayOutputStream compressPdf(InputStream original) throws Exception {

        File inputFile = File.createTempFile("input-", ".pdf");
        File outputFile = File.createTempFile("output-", ".pdf");

        try {
            try (InputStream is = original;
                    FileOutputStream fos = new FileOutputStream(inputFile)) {

                is.transferTo(fos);
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "gs",
                    "-sDEVICE=pdfwrite",
                    "-dCompatibilityLevel=1.4",
                    "-dPDFSETTINGS=/screen",
                    "-dNOPAUSE",
                    "-dQUIET",
                    "-dBATCH",
                    "-sOutputFile=" + outputFile.getAbsolutePath(),
                    inputFile.getAbsolutePath());

            pb.redirectErrorStream(true);

            Process process = pb.start();

            InputStream processOutput = process.getInputStream();
            String logs = new String(processOutput.readAllBytes());
            System.out.println("GS LOG: " + logs);

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Ghostscript falló con código: " + exitCode);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (InputStream is = new FileInputStream(outputFile)) {
                is.transferTo(output);
            }

            return output;

        } finally {
            inputFile.delete();
            outputFile.delete();
        }
    }

    @Async("fileExecutor")
    public void optimizeAsync(FileObject file) {
        try {
            optimize(file);
        } catch (Exception e) {
            e.printStackTrace();

            file.setStatus(FileStatus.FAILED);
            fileObjectService.save(file);
        }

    }

}