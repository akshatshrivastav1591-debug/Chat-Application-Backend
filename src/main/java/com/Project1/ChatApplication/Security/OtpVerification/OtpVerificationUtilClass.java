package com.Project1.ChatApplication.Security.OtpVerification;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
@Component
public class OtpVerificationUtilClass {
    public String otp() {
        SecureRandom otp = new SecureRandom();
        int Otp = 1000 + otp.nextInt(9000);
        return String.valueOf(Otp);
    }
}




