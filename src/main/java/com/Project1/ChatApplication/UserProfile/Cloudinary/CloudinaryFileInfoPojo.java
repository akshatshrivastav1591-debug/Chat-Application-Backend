package com.Project1.ChatApplication.UserProfile.Cloudinary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryFileInfoPojo {
    @Id
    private String fileURl;
    private String publicId;
    private Long fileSize;
    private String fileType;
}
