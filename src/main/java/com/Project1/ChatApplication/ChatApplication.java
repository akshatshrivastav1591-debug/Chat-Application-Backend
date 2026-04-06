package com.Project1.ChatApplication;

import com.Project1.ChatApplication.Security.OtpVerification.TwilioConfig.TwilioConfig;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
	@Autowired
	private TwilioConfig twilioConfig;
	@PostConstruct
public  void initTwilio(){
		Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
	}




	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);

		System.out.println("Welcome to the project akshat:");
		System.out.println("Relax,Things will work out:");
	}

}
