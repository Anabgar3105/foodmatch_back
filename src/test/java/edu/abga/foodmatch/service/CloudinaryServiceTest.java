package edu.abga.foodmatch.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CloudinaryService}.
 * Covers the uploadImage method and error propagation.
 */
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    /**
     * Tests that uploadImage returns the secure_url from Cloudinary response.
     * @throws IOException if the mock throws it
     */
    @Test
    void uploadImage_ReturnsSecureUrl() throws IOException {
        byte[] fileBytes = new byte[]{1, 2, 3};
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/test.jpg");
        when(uploader.upload(any(), anyMap())).thenReturn(uploadResult);

        String url = cloudinaryService.uploadImage(multipartFile, "recipes");
        assertEquals("https://cloudinary.com/test.jpg", url);
    }

    /**
     * Tests that uploadImage propagates IOException thrown by Cloudinary.
     */
    @Test
    void uploadImage_ThrowsIOException() throws IOException {
        when(multipartFile.getBytes()).thenThrow(new IOException("IO error"));
        assertThrows(IOException.class, () -> cloudinaryService.uploadImage(multipartFile, "recipes"));
    }
}

