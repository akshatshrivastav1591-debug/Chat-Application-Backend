package com.Project1.ChatApplication.UserProfile.Cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<Object,Object> uploadImage(MultipartFile imageFile) throws IOException;
    Map<Object,Object> uploadVideo(MultipartFile videoFile) throws  IOException;
    Map<Object,Object> uploadRawFile(MultipartFile file) throws  IOException;
}

