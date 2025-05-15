package com.ufpb.br.apps4society.my_trace_table_manager.service;

import io.minio.*;
import io.minio.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final int EXPIRATION_TIME_IN_SECONDS = 86400;

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    public String uploadFile(MultipartFile file) throws Exception {
        logger.info("Iniciando o upload do arquivo: {}", file.getOriginalFilename());
        try {
            boolean isExist = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!isExist) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            String uniqueName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            }
            return uniqueName;
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo: {}", e.getMessage(), e);
            throw new Exception("Erro ao fazer upload da imagem", e);
        }
    }

    public String getFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(EXPIRATION_TIME_IN_SECONDS)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar URL do arquivo no MinIO", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            logger.info("Arquivo removido com sucesso: {}", fileName);
        } catch (Exception e) {
            logger.error("Erro ao remover o arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover o arquivo do MinIO", e);
        }
    }
}
