package com.Project1.ChatApplication.MessageDao;

import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryFileInfoPojo;
import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryRepo;
import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@Service
public class CloudinaryServiceImplForMulitPartRequest implements CloudinaryService {
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
                    "folder", "ChatApplication-SendedPhoto-Folder",
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



            CloudinaryFileInfoPojo cloudinaryFileInfoPojo=new CloudinaryFileInfoPojo();
            cloudinaryFileInfoPojo.setFileURl(secureUrl);
            cloudinaryFileInfoPojo.setFileSize(((Number)data.get("bytes")).longValue());
            cloudinaryFileInfoPojo.setPublicId(data.get("public_id").toString());
            cloudinaryFileInfoPojo.setFileType("Image");
            cloudinaryRepo.save(cloudinaryFileInfoPojo);
            return Map.of(
                    "fileUrl", secureUrl,
                    "isSaved",true
            );

        } catch (IOException e) {
           return  Map.of("isSaved",false,"Reason","Something Wrong with server:");
        }
    }
    //To upload vidoes
    public Map<Object,Object> uploadVideo(MultipartFile videoFile) {
        try {
            if (videoFile.getSize() > 50 * 1024 * 1024) { // 50MB limit
                return  Map.of("isSaved",false,"Reason","Video is Too large,Kindly Send video less than size of 50mb");
            }

            String contentType = videoFile.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
               return Map.of("isSaved",false,"Reason","File type MisMatched");
            }

            Map<?,?> data = this.cloudinary.uploader().upload(videoFile.getBytes(), Map.of(
                    "folder", "ChatApplication-Videos-Folder",
                    "resource_type", "video",
                    "transformation", new Transformation()
                            .quality("auto:low")   // auto:low is fine for chat videos
                            .fetchFormat("mp4"),   // normalize everything to mp4
                    "eager", List.of(              // pre-generate a compressed version eagerly
                            new Transformation()
                                    .width(720)
                                    .height(480)
                                    .crop("limit")
                                    .quality("auto:low")
                                    .fetchFormat("mp4")
                    ),
                    "eager_async", true            // don't block the upload response
            ));
            CloudinaryFileInfoPojo cloudinaryFileInfoPojo=new CloudinaryFileInfoPojo();
            cloudinaryFileInfoPojo.setFileURl(data.get("secure_url").toString());
            cloudinaryFileInfoPojo.setFileSize(((Number)data.get("bytes")).longValue());
            cloudinaryFileInfoPojo.setPublicId(data.get("public_id").toString());
            cloudinaryFileInfoPojo.setFileType("video");
            cloudinaryRepo.save(cloudinaryFileInfoPojo);
            return Map.of("fileUrl",data.get("secure_url"), "isSaved",true);

        } catch (IOException e) {
            return Map.of("isSaved",false,"Reason","Something went wrong with server");
        }
    }
    public Map<Object,Object> uploadRawFile(MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return  Map.of("isSaved",false,"Reason","File too large. Maximum allowed size is 10MB.");
            }

            String contentType = file.getContentType();
            List<String> allowedTypes = List.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain"
            );
            if (contentType == null || !allowedTypes.contains(contentType)) {
                return Map.of("isSaved",false,"Reason","Invalid file type. Allowed: PDF, DOC, DOCX, TXT.");
            }

            Map<?,?> data = this.cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "folder", "ChatApplication-Files-Folder",
                    "resource_type", "raw",         // raw = no transformation applied
                    "use_filename", true,            // preserve original filename
                    "unique_filename", true          // append unique suffix to avoid collisions
            ));

            CloudinaryFileInfoPojo cloudinaryFileInfoPojo=new CloudinaryFileInfoPojo();
            cloudinaryFileInfoPojo.setFileURl(data.get("secure_url").toString());
            cloudinaryFileInfoPojo.setFileSize(((Number)data.get("bytes")).longValue());
            cloudinaryFileInfoPojo.setPublicId(data.get("public_id").toString());
            cloudinaryFileInfoPojo.setFileType("Raw");
            cloudinaryRepo.save(cloudinaryFileInfoPojo);
            return Map.of("fileUrl",data.get("secure_url"), "isSaved",true);
        } catch (IOException e) {
            return Map.of("isSaved",false,"Reason","Something went wrong with server");
        }
    }
 public boolean deleteMultipartFile(String publicId,String fileType) throws  IOException {
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