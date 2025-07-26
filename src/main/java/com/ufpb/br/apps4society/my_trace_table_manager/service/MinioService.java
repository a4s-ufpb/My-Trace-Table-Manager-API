package com.ufpb.br.apps4society.my_trace_table_manager.service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;

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

    @Value("${minio.public-url}")
    private String publicUrl;

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    public String uploadFile(MultipartFile file) throws Exception {
        logger.info("Iniciando o upload do arquivo: {}", file.getOriginalFilename());
        try {
            boolean isExist = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());

            if (!isExist) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String uniqueName;
            do {
                uniqueName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            } while (objectExists(uniqueName));

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uniqueName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
            }
            logger.info("Arquivo {} enviado com sucesso para o bucket {}", uniqueName, bucketName);
            return uniqueName;
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo: {}", e.getMessage(), e);
            throw new Exception("Erro ao fazer upload da imagem", e);
        }
    }

    public String getObjectUrl(String objectName) {
        return publicUrl + "/" + bucketName + "/" + objectName;
    }

    public void deleteObject(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            logger.info("Arquivo removido com sucesso: {}", objectName);
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equals(e.errorResponse().code())) {
                logger.warn("Arquivo {} já não existe no MinIO", objectName);
                return;
            }
            logger.error("Erro ao remover o arquivo (erro de resposta): {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover o arquivo do MinIO", e);
        } catch (Exception e) {
            logger.error("Erro ao remover o arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover o arquivo do MinIO", e);
        }
    }

    private boolean objectExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse() != null ? e.errorResponse().code() : null)) {
                return false;
            }
            logger.error("Erro ao verificar a existência do arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao verificar a existência do arquivo no MinIO", e);
        } catch (Exception e) {
            logger.error("Erro ao verificar a existência do arquivo: {}", e.getMessage(), e);
            return false;
        }
    }
}
