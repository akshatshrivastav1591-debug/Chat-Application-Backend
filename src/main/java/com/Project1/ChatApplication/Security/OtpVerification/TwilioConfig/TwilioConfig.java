package com.Project1.ChatApplication.Security.OtpVerification.TwilioConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix ="twilio")
@Data
public class TwilioConfig {

    private String accountSid;
    private String authToken;
    private String trialNumber;

}
