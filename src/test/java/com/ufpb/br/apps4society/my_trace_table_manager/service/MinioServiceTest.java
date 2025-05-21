package com.ufpb.br.apps4society.my_trace_table_manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;

public class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
    }

    @Test
    void uploadFileSuccessfully() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("arquivo.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo".getBytes()));
        when(file.getSize()).thenReturn(8L);
        when(file.getContentType()).thenReturn("image/png");

        when(minioClient.bucketExists(any())).thenReturn(true);

        when(minioClient.statObject(any()))
                .thenReturn(null)
                .thenThrow(new RuntimeException("Arquivo não encontrado"));

        when(minioClient.putObject(any()))
                .thenReturn(null);

        String generatedName = minioService.uploadFile(file);

        assertTrue(generatedName.endsWith("arquivo.png"));
        verify(minioClient, times(1)).putObject(any());
        verify(minioClient, atLeastOnce()).bucketExists(any());
    }

    @Test
    void uploadFileThrowsExceptionWhenPutObjectFails() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("arquivo.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo".getBytes()));
        when(file.getSize()).thenReturn(8L);
        when(file.getContentType()).thenReturn("image/png");

        when(minioClient.bucketExists(any())).thenReturn(true);

        when(minioClient.statObject(any()))
                .thenThrow(new RuntimeException("Arquivo não encontrado"));

        doThrow(new RuntimeException("Falha no upload"))
                .when(minioClient).putObject(any());

        Exception exception = assertThrows(Exception.class, () -> {
            minioService.uploadFile(file);
        });

        assertEquals("Erro ao fazer upload da imagem", exception.getMessage());
    }

    @Test
    void uploadFileThrowsExceptionWhenBucketDoesNotExist() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("arquivo.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo".getBytes()));
        when(file.getSize()).thenReturn(8L);
        when(file.getContentType()).thenReturn("image/png");

        when(minioClient.bucketExists(any())).thenReturn(false);

        doThrow(new RuntimeException("Falha ao criar o bucket"))
                .when(minioClient).makeBucket(any());
        
        Exception exception = assertThrows(Exception.class, () -> {
            minioService.uploadFile(file);
        });
        
        assertEquals("Erro ao fazer upload da imagem", exception.getMessage());
    }

    @Test
    void generateUrlSuccessfully() throws Exception {
        String objectName = "arquivo.png";
        String fakeUrl = "http://localhost:9000/test-bucket/arquivo.png";

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(fakeUrl);

        String result = minioService.getObjectUrl(objectName);

        assertEquals(fakeUrl, result);
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void generateUrlWithException() throws Exception {
        String objectName = "arquivo.png";

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("Erro simulado"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> minioService.getObjectUrl(objectName));

        assertEquals("Erro ao gerar URL do arquivo no MinIO", exception.getMessage());
    }

    @Test
    void deleteObjectSuccess() throws Exception {
        String objectName = "arquivo.png";

        doNothing().when(minioClient).removeObject(any());

        minioService.deleteObject(objectName);
        verify(minioClient, times(1)).removeObject(any());
    }

    @Test
    void deleteObjectWithException() throws Exception {
        String objectName = "arquivo-inexistente.png";

        doThrow(new RuntimeException("Erro simulado"))
                .when(minioClient)
                .removeObject(any());

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> minioService.deleteObject(objectName));

        assertEquals("Erro ao remover o arquivo do MinIO", runtimeException.getMessage());
    }
}
