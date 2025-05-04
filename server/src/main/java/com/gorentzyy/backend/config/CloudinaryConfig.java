package com.gorentzyy.backend.config;

import com.cloudinary.Cloudinary;
import com.gorentzyy.backend.utils.CloudinaryUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private final CloudinaryUtils cloudinaryUtils;

    public CloudinaryConfig(CloudinaryUtils cloudinaryUtils) {
        this.cloudinaryUtils = cloudinaryUtils;
    }

    @Bean
    public Cloudinary getCloudinary(){
        Map map= new HashMap<>();
        map.put("cloud_name", cloudinaryUtils.getCLOUDINARY_CLOUD_NAME());
        map.put("api_key",cloudinaryUtils.getCLOUDINARY_API_KEY());
        map.put("api_secret",cloudinaryUtils.getCLOUDINARY_API_SECRET());
        map.put("secure",true);
        return new Cloudinary(map);
    }
}
