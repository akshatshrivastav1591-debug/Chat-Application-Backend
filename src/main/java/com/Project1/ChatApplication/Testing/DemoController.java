package com.Project1.ChatApplication.Testing;

import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {
    @Autowired
    USerIdUtilMethods userId;
    @GetMapping("/demogreet")
    public String DemoWelcome(){
        return "welcome user:";
    }
//    @PostMapping("/demogreet")
//    public String DemoGreet(@RequestBody UserSecurityPojo user) {
//        return "hello "+user.getMobileno() +" password:"+user.getPassword()+" ok tested";
//    }

    @GetMapping("/testing")
    public ResponseEntity<?> extractingDetailsFromCookies(){
        return ResponseEntity.ok(Map.of("userID",userId.extractUserIdFromJwtToken()));
    }
}
