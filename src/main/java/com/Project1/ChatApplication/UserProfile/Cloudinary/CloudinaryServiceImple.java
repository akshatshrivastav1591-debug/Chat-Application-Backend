package com.Project1.ChatApplication.UserProfile.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImple implements  CloudinaryService {
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    CloudinaryRepo cloudinaryRepo;
    public Map<Object,Object> uploadImage(MultipartFile imageFile) {
        try {
            // Validate file size before uploading (fail fast, saves bandwidth)
            if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB hard limit
                return  Map.of("isSaved",false,"Reason","file is to large:");
            }


            String contentType = imageFile.getContentType();

            // Only allow image types
            if (contentType == null || !contentType.startsWith("image/")) {
                return Map.of("isSaved",false,"Reason","File Type Not matched:");
            }

            Map data = this.cloudinary.uploader().upload(imageFile.getBytes(), Map.of(
                    "folder", "ChatApplication-ProfilePhoto-Folder",
                    "transformation", new Transformation()
                            .width(300)
                            .height(300)
                            .crop("fill")        // fills the 300x300 box (better for avatars than "limit")
                            .gravity("face")     // centers crop on detected face
                            .quality("auto:best") // smarter quality — auto:best vs auto:low based on content
                            .fetchFormat("auto"), // converts to WebP/AVIF automatically (biggest size win)
                    "resource_type", "image",
                    "overwrite", true,           // prevent duplicate uploads for same user
                    "unique_filename", false
            ));

            // Always use secure_url (HTTPS)
            String secureUrl = (String) data.get("secure_url");
            long fileSize = ((Number) data.get("bytes")).longValue();

//            System.out.printf("Uploaded: %s | Size: %.2f KB%n", secureUrl, fileSize / 1024.0);
            CloudinaryFileInfoPojo cloudinaryFileInfoPojo=new CloudinaryFileInfoPojo();
            cloudinaryFileInfoPojo.setFileURl(secureUrl);
            cloudinaryFileInfoPojo.setFileSize(((Number)data.get("bytes")).longValue());
            cloudinaryFileInfoPojo.setPublicId(data.get("public_id").toString());
            cloudinaryFileInfoPojo.setFileType("Image");
            cloudinaryRepo.save(cloudinaryFileInfoPojo);
            return Map.of(
                    "url", secureUrl,
                     "isSaved",true
            );

        } catch (IOException e) {
            System.out.println("reason="+e.getLocalizedMessage());
            return  Map.of("isSaved",false,"Reason","Something Wrong with server:");
        }
    }

    @Override
    public Map<Object, Object> uploadVideo(MultipartFile videoFile) throws IOException {
        return null;
    }

    @Override
    public Map<Object, Object> uploadRawFile(MultipartFile file) throws IOException {
        return null;
    }

    public boolean deleteMultipartFile(String publicId,String fileType)  {
        try {


            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", fileType)
            );
            // result will contain {"result": "ok"} or {"result": "not found"}
            return true;
        }catch (IOException e){
            return  false;
        }

    }
}
