package com.Project1.ChatApplication.UserContacts;

import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserContactsUtil {
    @Autowired
    PhoneNumberFormatter formatter;
    @Autowired
    USerIdUtilMethods userIdUtil;


public Map<String,Object> authenticateContactNO(String contactNumber){
    String formattedContactNumber= formatter.contactNumberFormatter(contactNumber);
    if(formattedContactNumber.equals("Please enter ContactNumber")
    ||formattedContactNumber.equals("Invalid Number")
            || formattedContactNumber.equals("Please enter correct contactNumber:"))
    return Map.of("Response",formattedContactNumber,"status",false);
    else{
        String userID=userIdUtil.getUserIdForContacts(formattedContactNumber);
        if(userID.equals("This Person don't use our app,Kindly invite them on our app:"))
            return  Map.of("Response",userIdUtil.getUserIdForContacts(formattedContactNumber),"status",false);
        return Map.of("Response",userIdUtil.getUserIdForContacts(formattedContactNumber),"status",true);
    }
}

}
