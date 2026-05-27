package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.exception.ErrorCode;
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
import java.security.Principal;
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
     * @param principal the security principal containing the authenticated user's information
     * @param file the multipart file (image)
     * @param folder the folder in Cloudinary where the image will be stored (default: "recipes")
     * @return JSON with the URL of the uploaded image in Cloudinary
     */
    @PostMapping("/upload")
    @Operation(summary = "Subir archivo multimedia", description = "Sube cualquier imagen a Cloudinary y devuelve la URL")
    public ResponseEntity<Map<String, String>> uploadImage(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "recipes") String folder) {

        try {
            String publicId = null;

            if ("avatars".equals(folder) && principal != null) {
                publicId = principal.getName() + "_avatar";
            }
            String imageUrl = cloudinaryService.uploadImage(file, folder,publicId);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new FoodMatchException(ErrorCode.IMAGE_UPLOAD_FAILED, "Error al procesar y subir la imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}