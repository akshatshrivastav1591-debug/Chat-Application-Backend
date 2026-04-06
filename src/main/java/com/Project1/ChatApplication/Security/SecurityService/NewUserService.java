package com.Project1.ChatApplication.Security.SecurityService;


import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.SecurityRepo.UserSecurityRepo;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;

import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NewUserService {
    @Autowired
    UserProfileService createdProfile;
    @Autowired
    private USerIdUtilMethods util;
    @Autowired
    private UserSecurityRepo newUser;
    @Autowired
    private BCryptPasswordEncoder encoder;
    PhoneNumberFormatter Formatter = new PhoneNumberFormatter();


    public ResponseEntity<?> addNewUser(UserSecurityPojoClass user) {
        String authenticateMobileNumber = Formatter.authenticateMobileNumber(user);

        if (authenticateMobileNumber.equals("please enter all the credentials:") ||
                authenticateMobileNumber.equals("Mobile Numbers Can't contains other values:") ||
                authenticateMobileNumber.equals("Invalid Phone Number"))

            return ResponseEntity.status(401).body(authenticateMobileNumber) ;
        else {
            if (newUser.existsById(authenticateMobileNumber)) {
                return ResponseEntity.status(409).body("User already exists:");
            }
            //-->Saving the user info in the table
            user.setMobileno(authenticateMobileNumber);
            user.setPassword(encoder.encode(user.getPassword()));
            newUser.save(user);

            //-->Genrating the userID for future Usage
               util.UserIdGenerator(authenticateMobileNumber);


            return ResponseEntity.ok("User Registered:");
        }
    }
public  String updatePassword(Map<String,String> newPassword){
        UserSecurityPojoClass oldPassword=newUser.findBymobileno(newPassword.get("mobileNo"));

      if(newPassword.get("newPassword").isEmpty()||newPassword.get("confirmPassword").isEmpty()
              ||newPassword.get("newPassword").isBlank()||newPassword.get("confirmPassword").isBlank())
          return "Please Enter All the Fields:";
      if(!newPassword.get("newPassword").equals(newPassword.get("confirmPassword"))) {
          return " Entered Confirm Password is not matched";}
      if(encoder.matches(newPassword.get("newPassword"), oldPassword.getPassword())) return "Please enter a different Password than old one:";
    oldPassword.setPassword(encoder.encode(newPassword.get("newPassword")));
    oldPassword.setMobileno(newPassword.get("mobileNo"));
    newUser.save(oldPassword);

    return  "Password Updated Successfull:";
}


}

