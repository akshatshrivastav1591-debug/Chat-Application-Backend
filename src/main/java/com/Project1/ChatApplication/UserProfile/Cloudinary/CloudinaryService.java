package com.Project1.ChatApplication.UserProfile.Cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile imageFile) throws IOException;
}

