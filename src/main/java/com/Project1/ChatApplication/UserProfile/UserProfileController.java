package com.Project1.ChatApplication.UserProfile;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;


@CrossOrigin(origins = "http://localhost:5174")
@RestController
public class UserProfileController {
    @Autowired
    private UserProfileService saveProfile;
    @PostMapping(value="/userProfile")
    public ResponseEntity<?> saveUserProfilePicture(@RequestParam("imageFile")MultipartFile imageFile,@RequestParam("userData")String userDatasJson)throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        UserProfilePojoClass userData =
                mapper.readValue(userDatasJson, UserProfilePojoClass.class);
        System.out.println("ProfilePicture:"+imageFile.getContentType());
        System.out.println("Parsed User: " + userData);


        return saveProfile.finishedNewlyCreatedProfile(imageFile,userData);
    }
}
