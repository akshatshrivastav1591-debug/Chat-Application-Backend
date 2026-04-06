package com.Project1.ChatApplication.Security.UserIdGeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
public class USerIdUtilMethods {
    @Autowired
    UserIdRepo newUserID;
       //-->Using by Register to generate and save user id ,One Per MobileNO:
    public void UserIdGenerator(String mobileNO) {
        UserIdPojoClass userId = new UserIdPojoClass();
        userId.setMobileNo(mobileNO);
        userId.setFirstlogin(true);
        newUserID.save(userId);
    }

    public Map<String, Object> getUserID(String mobileNo) {
        UserIdPojoClass userId = newUserID.findByMobileNo(mobileNo);
//        Map<String,Boolean> userIdMap=Map.of("UserId",userId.getExternalUserID(),"firstLogin:",userId.isFirstlogin());

        return Map.of("UserId",userId.getExternalUserID(),"FirstLogin",userId.isFirstlogin());
    }
    public String getUserMobileNo(String userId){
        UserIdPojoClass mobileNo=newUserID.findByexternalUserID(userId);
        return  mobileNo.getMobileNo();
    }
    public String extractUserIdFromJwtToken(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        Map<String,Object> userdetails= (Map<String, Object>) authentication.getDetails();
        return userdetails.get("userID").toString();
    }

    public String getUserIdForContacts(String contactNO){
        UserIdPojoClass userID=newUserID.findByMobileNo(contactNO);
        if(userID==null) return "This Person don't use our app,Kindly invite them on our app:";
        else return userID.getExternalUserID();
    }

}
