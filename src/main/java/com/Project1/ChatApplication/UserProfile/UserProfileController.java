package com.Project1.ChatApplication.UserProfile;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RestController
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;
    @PostMapping(value="/userProfile")
    public ResponseEntity<?> saveUserProfilePicture(@RequestBody UserProfilePojoClass userInfo)throws Exception{
        return userProfileService.finishedNewlyCreatedProfile(userInfo);
    }
    @GetMapping(value = "/getMyProfile")
    public ResponseEntity<Map<String,UserProfileDtoClass>> getUserProfile(){
        return userProfileService.getUserProfile();
    }
     @PutMapping(value = "/updateUserProfile")
    public  ResponseEntity<Map<String,String>> updateUserProfile(@RequestBody UserProfilePojoClass updatedUserDetails){

        return userProfileService.updateUserProfile(updatedUserDetails);
     }
}
