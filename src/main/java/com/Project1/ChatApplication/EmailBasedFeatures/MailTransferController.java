package com.Project1.ChatApplication.EmailBasedFeatures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RestController
public class MailTransferController {
    @Autowired
    MailTransferServiceClass mailTransferServiceClass;

    @PostMapping("/sendFeedbackMail")
   public  ResponseEntity<Map<String,String>> sendFeedbackMessage(@RequestBody Map<String,String>feedbackObject){

       return mailTransferServiceClass.saveFeedbackMessage(feedbackObject);
    }

    @PostMapping("/reportingOfAnIssue")
    public  ResponseEntity<Map<String,String>> reportAnIssue(@RequestBody Map<String,String>feedbackObject){

        return mailTransferServiceClass.reportAnIssue(feedbackObject);
    }

}
