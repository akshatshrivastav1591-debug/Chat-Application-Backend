package com.Project1.ChatApplication.Security.OtpVerification.UserOtpVerificationPojo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationPojoClass {

    @Id
    private String mobileno;
    private String  otp;
    private LocalDateTime expiredtime;
    private boolean expired;
}
