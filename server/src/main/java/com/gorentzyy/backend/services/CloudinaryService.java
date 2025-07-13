package com.gorentzyy.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile file);
    void delete(String publicId);
}
