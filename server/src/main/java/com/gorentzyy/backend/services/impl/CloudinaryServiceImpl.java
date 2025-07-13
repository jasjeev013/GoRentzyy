package com.gorentzyy.backend.services.impl;

import com.cloudinary.Cloudinary;
import com.gorentzyy.backend.exceptions.CloudinaryUploadException;
import com.gorentzyy.backend.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {


    private static final String UPLOAD_FAILED_MSG = "Failed to upload file to Cloudinary: ";

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, Object> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to upload null or empty file");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            log.debug("Attempting to upload file: {}", file.getOriginalFilename());

            // Validate file size (e.g., 10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                log.warn("File size exceeds limit: {}", file.getOriginalFilename());
                throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
            }

            // Validate content type
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                log.warn("Invalid file type attempted: {}", contentType);
                throw new IllegalArgumentException("Only image files are allowed");
            }

            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("resource_type", "auto"); // Auto-detect resource type
            uploadOptions.put("quality", "auto:good"); // Optimize quality

            Map uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), uploadOptions);

            if (uploadResult == null || uploadResult.get("url") == null) {
                log.error("Cloudinary upload failed - no URL returned for file: {}",
                        file.getOriginalFilename());
                throw new CloudinaryUploadException(UPLOAD_FAILED_MSG);
            }

            log.info("Successfully uploaded file: {} to Cloudinary. Size: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            return uploadResult;

        } catch (IOException e) {
            log.error("IO error uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new CloudinaryUploadException(UPLOAD_FAILED_MSG+ e);
        } catch (RuntimeException e) {
            log.error("Cloudinary API error uploading file {}: {}",
                    file.getOriginalFilename(), e.getMessage());
            throw new CloudinaryUploadException(UPLOAD_FAILED_MSG+e);
        } catch (Exception e) {
            log.error("Unexpected error uploading file {}: {}",
                    file.getOriginalFilename(), e.getMessage());
            throw new CloudinaryUploadException(UPLOAD_FAILED_MSG+e);
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            log.warn("Attempted to delete with null or empty publicId");
            throw new IllegalArgumentException("Public ID cannot be null or empty");
        }

        try {
            log.debug("Attempting to delete file with publicId: {}", publicId);

            Map deleteResult = cloudinary.uploader().destroy(publicId, Map.of());

            if (!"ok".equals(deleteResult.get("result"))) {
                log.error("Failed to delete file with publicId: {}. Response: {}",
                        publicId, deleteResult);
                throw new CloudinaryUploadException("Failed to delete file from Cloudinary");
            }

            log.info("Successfully deleted file with publicId: {}", publicId);

        } catch (IOException e) {
            log.error("IO error deleting file with publicId {}: {}", publicId, e.getMessage());
            throw new CloudinaryUploadException("Failed to delete file from Cloudinary: "+e);
        } catch (RuntimeException e) {
            log.error("Cloudinary API error deleting file with publicId {}: {}",
                    publicId, e.getMessage());
            throw new CloudinaryUploadException("Failed to delete file from Cloudinary: "+ e);
        }
    }
}