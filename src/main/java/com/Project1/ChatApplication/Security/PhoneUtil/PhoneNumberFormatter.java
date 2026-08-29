package com.Project1.ChatApplication.Security.PhoneUtil;

import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {
    PhoneNumberUtil util;
  public  PhoneNumberFormatter(){
       util=PhoneNumberUtil.getInstance();
  }
    private  boolean IsEmpty(UserSecurityPojoClass user) {
        return  user.getMobileno() == null || user.getPassword() == null ||user.getMobileno().isBlank() || user.getPassword().isBlank();

    }


    public String authenticateMobileNumber(UserSecurityPojoClass userdata){
        if (userdata.getMobileno() == null || userdata.getPassword() == null ||
                userdata.getMobileno().isBlank() || userdata.getPassword().isBlank()) {
            return null;
        }
        else{
            try {
                Phonenumber.PhoneNumber number=util.parse(userdata.getMobileno(),"IN");
                String formatedphone=util.format(number,PhoneNumberUtil.PhoneNumberFormat.E164);
                if(!util.isValidNumber(number))
                    return null;
                else{
                    return formatedphone;
                }
            } catch (NumberParseException e) {

                return null;
            }




        }


    }
    public  String numberFormatter(UserSecurityPojoClass userdata){
        if(IsEmpty(userdata))
            return "Please enter all credentials:";
        try {
            Phonenumber.PhoneNumber number=util.parse(userdata.getMobileno(),"IN");
            return  util.format(number,PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {

            return "Mobile Numbers Can't contains other values:";
        }
  }
  public String contactNumberFormatter(String contactNumber){
      if(contactNumber==null||contactNumber.isBlank()) return "Please enter ContactNumber";
      else{
          try {
              Phonenumber.PhoneNumber number=util.parse(contactNumber,"IN");

              if(util.isValidNumber(number)){
                 return util.format(number,PhoneNumberUtil.PhoneNumberFormat.E164);
              }
              else{
                  return "Invalid Number";
              }
          }catch (NumberParseException e){

              return "Please enter correct contactNumber:";
          }
      }
  }
}
