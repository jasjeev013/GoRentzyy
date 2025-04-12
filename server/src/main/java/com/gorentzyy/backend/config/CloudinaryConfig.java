package com.gorentzyy.backend.config;

import com.cloudinary.Cloudinary;
import com.gorentzyy.backend.constants.SecretConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary(){
        Map map= new HashMap<>();
        map.put("cloud_name", SecretConstants.CLOUDINARY_CLOUD_NAME);
        map.put("api_key",SecretConstants.CLOUDINARY_API_KEY);
        map.put("api_secret",SecretConstants.CLOUDINARY_API_SECRET);
        map.put("secure",true);
        return new Cloudinary(map);
    }
}
