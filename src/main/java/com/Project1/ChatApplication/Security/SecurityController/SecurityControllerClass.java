package com.Project1.ChatApplication.Security.SecurityController;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterServiceClass;
import com.Project1.ChatApplication.Security.OtpVerification.Service.OtpVerificationServiceClass;
import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.SecurityService.NewUserService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SecurityControllerClass {
@Autowired
private NewUserService newEntry;
@Autowired
    AuthenticationManager authenticationManager;
@Autowired
JwtFilterServiceClass jwt;
@Autowired
 private OtpVerificationServiceClass otpService;
@Autowired
 private USerIdUtilMethods getUserId;

PhoneNumberFormatter Numberformatter=new PhoneNumberFormatter();

    @GetMapping("/greet") //Demo greet Method:
    public String DemoGreet(){
        return  "Hello user,Welcome to my chat app:";
    }


      @PostMapping("/login") //user Login Method:
    public ResponseEntity<?> UserLogin(@RequestBody UserSecurityPojoClass user, HttpServletResponse response) {

          String formatter = Numberformatter.numberFormatter(user);
          if (formatter.equals("Please enter all credentials:") || formatter.equals("Numbers Can't contains other values:"))
              return ResponseEntity.status(401).body(formatter);
          else {
              Authentication authorized = authenticationManager
                      .authenticate(new UsernamePasswordAuthenticationToken(formatter, user.getPassword()));

              if (authorized.isAuthenticated()) {
                               Map<String,Object> userDetails=getUserId.getUserID(formatter);

                      String token =jwt.getJwtTokens(formatter,userDetails.get("UserId").toString(),(boolean)(userDetails.get("FirstLogin")));
                      Cookie cookie=new Cookie("jwt",token);
                      cookie.setHttpOnly(true);
                      cookie.setSecure(true);
                      cookie.setPath("/");
                      cookie.setMaxAge(60*60);
                      response.addCookie(cookie);

                  return ResponseEntity.ok("WelcomeBack User:");
              } else {
                  return ResponseEntity.status(401).body("Invalid User,Authorize denied:");
              }
          }
      }

@PostMapping("/register") //User Register Method:
public ResponseEntity<?> UserRegister(@RequestBody UserSecurityPojoClass newUserData)  {
    return  newEntry.addNewUser(newUserData);
    }

@PostMapping("/forgotpassword/generateotp")
    public String updatePassword(@RequestBody UserSecurityPojoClass phonenumber){


    return  otpService.otpGeneration(phonenumber);
}
@PostMapping("/forgotpassword/validatingotp")
    public String validatingOtp(@RequestBody Map<String,String>otpDetails)throws  Exception{


        return otpService.otpValidation(otpDetails);
}
}


