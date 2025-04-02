package com.gorentzyy.backend.services.impl;

import com.cloudinary.Cloudinary;
import com.gorentzyy.backend.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map upload(MultipartFile file) {
        try{
            Map data = cloudinary.uploader().upload(file.getBytes(),Map.of());
            System.out.println(data);
            return data;

        }catch (IOException e){
            throw  new RuntimeException("Image Uploading Failed");
        }
    }
}
