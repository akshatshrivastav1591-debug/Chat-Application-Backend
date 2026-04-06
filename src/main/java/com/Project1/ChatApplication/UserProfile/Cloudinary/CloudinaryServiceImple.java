package com.Project1.ChatApplication.UserProfile.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImple implements  CloudinaryService {
    @Autowired
    Cloudinary cloudinary;
    public Map upload(MultipartFile imageFile) {
        try {
            Map data= this.cloudinary.uploader().upload(imageFile.getBytes(),Map.of(
                    "folder","ChatAppliacation-ProfilePhoto-Folder",
                    "transformation",new Transformation()
                            .height(300)
                            .width(300)
                            .crop("limit")
                            .quality("auto")
            ));
            System.out.println("url:"+data.get("url"));
            return Map.of("url",data.get("url"));
        }catch (IOException e){
            throw new RuntimeException("Image uploading failed,Reason->"+e.getLocalizedMessage());
        }


    }
}
