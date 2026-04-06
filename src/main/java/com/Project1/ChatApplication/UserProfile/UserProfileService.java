package com.Project1.ChatApplication.UserProfile;

import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {
    @Autowired
    UserProfileRepo profileRepo;
    @Autowired
    CloudinaryServiceImple cloudinary;
    @Autowired
    USerIdUtilMethods userIdUtil;
    public String newCreatedProfile(String userId,String mobileNo){
        UserProfilePojoClass createdProfile=new UserProfilePojoClass();
        createdProfile.setUserID(userId);
        createdProfile.setMobileNo(mobileNo);
        profileRepo.save(createdProfile);
        return userId;
    }
    public ResponseEntity<?> finishedNewlyCreatedProfile(MultipartFile imageFile,UserProfilePojoClass userProfileData){
      try {
          Map imageUrl= cloudinary.upload(imageFile);
          String userID=userIdUtil.extractUserIdFromJwtToken();
          String mobileNo= userIdUtil.getUserMobileNo(userIdUtil.extractUserIdFromJwtToken());
          userProfileData.setUserID(userID);
          userProfileData.setImageUrl(imageUrl.get("url").toString());
          userProfileData.setMobileNo(mobileNo);
          profileRepo.save(userProfileData);
          return ResponseEntity.ok("User Profile successfully Created:");
      }catch (Exception e){
          return ResponseEntity.status(500).body("Something went Wrong,Reason:"+e.getLocalizedMessage());
      }

    }
  //Util Methods for UserContactServiceClass
    public String getProfileImage(String userID){
         UserProfilePojoClass userData= profileRepo.findById(userID).orElseThrow(()-> new RuntimeException("User Not found:"));
        return userData.getImageUrl();
    }

    public String getMobileNo(String userID){
        UserProfilePojoClass userData= profileRepo.findById(userID).orElseThrow(()-> new RuntimeException("User Not found:"));
        return userData.getMobileNo();
    }

}
