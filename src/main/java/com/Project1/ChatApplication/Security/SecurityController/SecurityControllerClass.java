package com.Project1.ChatApplication.Security.SecurityController;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterServiceClass;

import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.SecurityService.NewUserService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.Security.UserIdGeneration.UserIdPojoClass;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins}",allowCredentials = "true")
public class SecurityControllerClass {
@Autowired
private NewUserService newEntry;
@Autowired
    AuthenticationManager authenticationManager;
@Autowired
JwtFilterServiceClass jwt;

@Autowired
 private USerIdUtilMethods getUserId;

PhoneNumberFormatter Numberformatter=new PhoneNumberFormatter();

    @GetMapping("/authenticatingJwtToken") //Demo greet Method:
    public ResponseEntity<Object> DemoGreet(){

        return ResponseEntity.ok(Map.of("IsAuthenticated","Jwt is authenticated:"));
    }


      @PostMapping("/login") //user Login Method:
    public ResponseEntity<?> UserLogin(@RequestBody UserSecurityPojoClass user, HttpServletResponse response) {

          String formatter = Numberformatter.authenticateMobileNumber(user);
          if (formatter == null)
              return ResponseEntity.status(401).body(Map.of("message", "Invalid or Empty Credentials"));
          else {
              try {


                  Authentication authorized = authenticationManager
                          .authenticate(new UsernamePasswordAuthenticationToken(formatter, user.getPassword()));

                  if (authorized.isAuthenticated()) {
                      Map<String, Object> userDetails = getUserId.getUserID(formatter);

                      String token = jwt.getJwtTokens(formatter, userDetails.get("UserId").toString());
//                      Cookie cookie = new Cookie("jwt", token);
//                      cookie.setHttpOnly(true);
//                      cookie.setSecure(false);
//                      cookie.setPath("/");
//                      cookie.setMaxAge(60 * 60);
                      response.setHeader("Set-Cookie",
                              "jwt=" + token +
                                      "; Path=/" +
                                      "; Max-Age=3600" +
                                      "; HttpOnly" +
                                      "; SameSite=None"+
                                      "; Secure"
                              // ✅ No Secure flag — works on HTTP localhost
                      );
                        UserIdPojoClass firstLogin=getUserId.isFirstLogin(formatter);
                        if(firstLogin==null)return ResponseEntity.ok(Map.of("message", "WelcomeBack User:","firstLogin",false,"currentUserID",userDetails.get("UserId").toString()));
                      else{
                          firstLogin.setFirstlogin(false);
                          boolean confirmation=getUserId.updatedUserID(firstLogin);
                          if(confirmation) return ResponseEntity.ok(Map.of("message","welcome User to the Chatrix App:","firstLogin",true));
                          return   ResponseEntity.status(500).body(Map.of("message","Something wrong with first login:"));
                        }

                  } else {
                      return ResponseEntity.status(401).body(Map.of("message", "Invalid User,Authorize denied:"));
                  }
              }catch(BadCredentialsException e){

                  return  ResponseEntity.status(401).body(Map.of("message","Wrong MobileNo or Password:"));
              }
          }
      }
@PostMapping("/register") //User Register Method:
public ResponseEntity<?> UserRegister(@RequestBody UserSecurityPojoClass newUserData)  {




    return  newEntry.addNewUser(newUserData);
    }
@GetMapping("/getCurrentUserId")
    public  ResponseEntity<Object> currentUserID(){
        return ResponseEntity.ok(Map.of("currentUserID",getUserId.extractUserIdFromJwtToken()));
    }
@PostMapping("/userLogout")
    public ResponseEntity<Map<String,String>> userLogout(HttpServletResponse response){
        try {

            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(0)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(Map.of("message", "user logout is  a success:"));
        }catch (Exception e){

            return null;
        }

}


}


