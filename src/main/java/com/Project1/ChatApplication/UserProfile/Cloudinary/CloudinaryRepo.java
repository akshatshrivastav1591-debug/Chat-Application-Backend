package com.Project1.ChatApplication.UserProfile.Cloudinary;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudinaryRepo extends JpaRepository<CloudinaryFileInfoPojo,String> {
}
