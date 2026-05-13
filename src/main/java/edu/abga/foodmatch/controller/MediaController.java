package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to manage multimedia files (images).
 * Provides an endpoint to upload images to Cloudinary and retrieve their URLs.
 */
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Multimedia", description = "Endpoints para la gestión de archivos e imágenes")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    /**
     * Endpoint to upload and image
     * @param file the multipart file (image)
     * @return JSON with the URL of the uploaded image in Cloudinary
     */
    @PostMapping("/upload")
    @Operation(summary = "Subir archivo multimedia", description = "Sube cualquier imagen a Cloudinary y devuelve la URL")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new FoodMatchException("Error al procesar y subir la imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}