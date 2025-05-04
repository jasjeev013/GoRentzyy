package com.gorentzyy.backend.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CloudinaryUtils {

    @Value("${api.cloudinary.cloud.name}")
    private String CLOUDINARY_CLOUD_NAME;
    @Value("${api.cloudinary.api.key}")
    private String CLOUDINARY_API_KEY;
    @Value("${api.cloudinary.api.secret}")
    private String CLOUDINARY_API_SECRET;
}
