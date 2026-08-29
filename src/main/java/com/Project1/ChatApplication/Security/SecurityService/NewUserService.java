package com.Project1.ChatApplication.Security.SecurityService;

import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.SecurityRepo.UserSecurityRepo;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NewUserService {

    @Autowired
    private USerIdUtilMethods util;
    @Autowired
    private UserSecurityRepo newUser;
    @Autowired
    private BCryptPasswordEncoder encoder;
    PhoneNumberFormatter Formatter = new PhoneNumberFormatter();


    public ResponseEntity<?> addNewUser(UserSecurityPojoClass user) {
        try {
            String authenticateMobileNumber = Formatter.authenticateMobileNumber(user);
            if (authenticateMobileNumber == null)

                return ResponseEntity.status(401).body(Map.of("message", "Invalid or Empty Credentials:"));
            else {
                if (newUser.existsById(authenticateMobileNumber)) {
                    return ResponseEntity.status(409).body(Map.of("message", "User already exists"));
                }
                //-->Saving the user info in the table
                user.setMobileno(authenticateMobileNumber);
                user.setPassword(encoder.encode(user.getPassword()));
                newUser.save(user);

                //-->Genrating the userID for future Usage
                util.UserIdGenerator(authenticateMobileNumber);


                return ResponseEntity.ok(Map.of("message", "User Registered:"));
            }
        } catch (Exception e) {
            return null;
        }
    }


}

