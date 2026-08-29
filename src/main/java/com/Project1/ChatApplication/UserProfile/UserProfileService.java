package com.Project1.ChatApplication.UserProfile;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;


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
    public ResponseEntity<?> finishedNewlyCreatedProfile(UserProfilePojoClass userInfo){
      try {

          userInfo.setUserID(userIdUtil.extractUserIdFromJwtToken());
          userInfo.setMobileNo(userIdUtil.getUserMobileNo(userIdUtil.extractUserIdFromJwtToken()));
          profileRepo.save(userInfo);
          return ResponseEntity.ok(Map.of("message","User Profile successfully Created:"));
      }catch (Exception e){

          return ResponseEntity.status(500).body(Map.of("message","Something went Wrong,Reason:"+e.getLocalizedMessage()));
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

    public ResponseEntity<Map<String,UserProfileDtoClass>> getUserProfile() {
      try {
        UserProfilePojoClass userProfilePojoClass = profileRepo.findByUserID(userIdUtil.extractUserIdFromJwtToken());

        if (userProfilePojoClass == null) {
            return ResponseEntity.status(500).body(null);
        }
        UserProfileDtoClass userProfileDtoClass = new UserProfileDtoClass();
        userProfileDtoClass.setImageUrl(userProfilePojoClass.getImageUrl());
        userProfileDtoClass.setFirstName(userProfilePojoClass.getFirstName());
        userProfileDtoClass.setLastName(userProfilePojoClass.getLastName());
        userProfileDtoClass.setAddress(userProfilePojoClass.getAddress());
        userProfileDtoClass.setDateOfBirth(userProfilePojoClass.getBirthDate());
        return ResponseEntity.ok(Map.of("userProfile", userProfileDtoClass));
    }catch (Exception e){
          return ResponseEntity.status(500).body(null);
      }
    }


    public ResponseEntity<Map<String,String>> updateUserProfile(UserProfilePojoClass updatedUserDetails)  {

        try {
            UserProfilePojoClass oldUserDetails = profileRepo.findByUserID(userIdUtil.extractUserIdFromJwtToken());
            if(oldUserDetails==null) {
                ResponseEntity<?> newCreatedProfile=finishedNewlyCreatedProfile(updatedUserDetails);
                if(newCreatedProfile.getStatusCode().is2xxSuccessful()){
                    return ResponseEntity.ok(Map.of("message","User Profile updated Successfully"));
                }
                else{
                    throw  new IOException();
                }
            }
            if ((updatedUserDetails.getImageUrl()==null||updatedUserDetails.getImageUrl().isEmpty()) || oldUserDetails.getImageUrl().equals(updatedUserDetails.getImageUrl())) {
                    updatedUserDetails.setUserID(oldUserDetails.getUserID());
                    updatedUserDetails.setMobileNo(oldUserDetails.getMobileNo());
                    updatedUserDetails.setImageUrl(oldUserDetails.getImageUrl());
                    updatedUserDetails.setImagePublicId(oldUserDetails.getImagePublicId());
                    updatedUserDetails.setImageType(oldUserDetails.getImageType());
                    if(updatedUserDetails.getFirstName()==null||updatedUserDetails.getFirstName().isEmpty()){
                        updatedUserDetails.setFirstName(oldUserDetails.getFirstName());
                    }
                   if(updatedUserDetails.getLastName()==null||updatedUserDetails.getLastName().isEmpty()){
                    updatedUserDetails.setLastName(oldUserDetails.getLastName());
                }
                if(updatedUserDetails.getAddress()==null||updatedUserDetails.getAddress().isEmpty()){
                    updatedUserDetails.setAddress(oldUserDetails.getAddress());
                }

                    profileRepo.save(updatedUserDetails);
                return ResponseEntity.ok(Map.of("message","User Profile updated Successfully"));
            }
            boolean isDeleted;
                    if((oldUserDetails.getImagePublicId()==null || oldUserDetails.getImagePublicId().isEmpty())
                    ||(oldUserDetails.getImageType()==null || oldUserDetails.getImageType().isEmpty())
                    ||(oldUserDetails.getImageUrl()==null || oldUserDetails.getImageUrl().isEmpty())){
                isDeleted=true;
            }
            else {
                isDeleted = cloudinary.deleteMultipartFile(oldUserDetails.getImagePublicId(), oldUserDetails.getImageType());
            }
            if (isDeleted) {
                updatedUserDetails.setUserID(oldUserDetails.getUserID());
                updatedUserDetails.setMobileNo(oldUserDetails.getMobileNo());
                if(updatedUserDetails.getFirstName()==null||updatedUserDetails.getFirstName().isEmpty()){
                    updatedUserDetails.setFirstName(oldUserDetails.getFirstName());
                }
                if(updatedUserDetails.getLastName()==null||updatedUserDetails.getLastName().isEmpty()){
                    updatedUserDetails.setLastName(oldUserDetails.getLastName());
                }
                if(updatedUserDetails.getAddress()==null||updatedUserDetails.getAddress().isEmpty()){
                    updatedUserDetails.setAddress(oldUserDetails.getAddress());
                }
                profileRepo.save(updatedUserDetails);
                return ResponseEntity.ok(Map.of("message","User Profile updated Successfully"));
            } else throw new IOException();
        }catch (Exception e){

            return  ResponseEntity.status(500).body(Map.of("message","Something went Wrong With Server"));
        }
    }
}
