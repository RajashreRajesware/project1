package com.example.real_estate_project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageUploadServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageUploadService imageUploadService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }


    @Test
    void testUploadImage_Success() throws Exception {
        byte[] fileBytes = "fake image bytes".getBytes();
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn(fileBytes);
        when(uploader.upload(eq(fileBytes), anyMap()))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/image.jpg"));

        String result = imageUploadService.uploadImage(file);

        assertEquals("https://res.cloudinary.com/demo/image.jpg", result);
        verify(uploader, times(1)).upload(eq(fileBytes), anyMap());
    }


    @Test
    void testUploadImage_EmptyFile_ThrowsException() throws Exception {
        when(file.isEmpty()).thenReturn(true);

        IOException exception = assertThrows(IOException.class, () -> imageUploadService.uploadImage(file));

        assertEquals("File is empty", exception.getMessage());
        verify(uploader, never()).upload(any(), anyMap());
    }


    @Test
    void testUploadImage_UploadFails_ThrowsIOException() throws Exception {
        byte[] fileBytes = "data".getBytes();
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn(fileBytes);
        when(uploader.upload(eq(fileBytes), anyMap())).thenThrow(new IOException("Cloudinary error"));

        IOException exception = assertThrows(IOException.class, () -> imageUploadService.uploadImage(file));

        assertEquals("Cloudinary error", exception.getMessage());
    }
}
