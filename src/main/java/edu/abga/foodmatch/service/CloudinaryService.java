package edu.abga.foodmatch.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service to manage the logic of uploading images to Cloudinary and retrieving their URLs.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    /**
     * The cloudinary instance used to interact with the Cloudinary API for uploading images.
     */
    private final Cloudinary cloudinary;

    /**
     * Upload Multipart file to Cloudinary and return its secure URL.
     * @param file Multipart file to be uploaded.
     * @return URL of the uploaded image.
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "foodmatch/" + folder)
        );

        return uploadResult.get("secure_url").toString();
    }
}