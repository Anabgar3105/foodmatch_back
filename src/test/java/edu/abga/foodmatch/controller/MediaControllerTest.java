package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MediaController}.
 * Covers the uploadImage endpoint and error handling.
 */
@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private Principal principal;

    @InjectMocks
    private MediaController mediaController;


    /**
     * Tests successful image upload returns the correct URL in the response.
     * @throws IOException if the mock throws it
     */
    @Test
    void uploadImage_ReturnsUrlOnSuccess() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String expectedUrl = "https://cloudinary.com/image.jpg";

        when(cloudinaryService.uploadImage(mockFile, "recipes", null)).thenReturn(expectedUrl);

        ResponseEntity<Map<String, String>> response = mediaController.uploadImage(principal, mockFile, "recipes");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().get("url"));
    }

    /**
     * Tests that an IOException thrown by the service is wrapped in a FoodMatchException.
     */
    @Test
    void uploadImage_ThrowsFoodMatchExceptionOnError() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(cloudinaryService.uploadImage(mockFile, "recipes", null)).thenThrow(new IOException("IO error"));

        Exception exception = assertThrows(FoodMatchException.class, () -> mediaController.uploadImage(principal, mockFile, "recipes"));
        assertTrue(exception.getMessage().contains("Error al procesar y subir la imagen"));
    }
}

