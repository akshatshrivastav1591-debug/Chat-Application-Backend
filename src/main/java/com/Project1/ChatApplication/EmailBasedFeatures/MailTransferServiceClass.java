package com.Project1.ChatApplication.EmailBasedFeatures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MailTransferServiceClass {
    private static final String feedbackSubject = "To Share Feedback of the Website";
    private static final String reportingAnIssuesSubject = "To Report any issue of the  Website:";
    private static final String feedbackReturnMessageBody = "Thanks for your feedback! We've received it and really appreciate you taking the time to help us improve.";
    private static final String reportingAnIssueReturnMessageBody = "Your issue has been reported. Thanks for flagging it — we'll look into it as soon as possible.";

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailTransferRepo mailTransferRepo;

    public boolean sendEmail(String toEmail, String subject, String body) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("akshatshrivastav1591@gmail.com");
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            mailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {

            return false;
        }

    }

    public ResponseEntity<Map<String, String>> saveFeedbackMessage(Map<String, String> feedbackObject) {
        try {
            if (sendEmail(feedbackObject.get("senderEmail"), feedbackSubject, feedbackReturnMessageBody)) {
                MailTransferPojoClass mailTransferPojoClass = new MailTransferPojoClass();
                mailTransferPojoClass.setSubject(feedbackSubject);
                mailTransferPojoClass.setBody(feedbackObject.get("message"));
                mailTransferPojoClass.setSenderEmailId(feedbackObject.get("senderEmail"));
                mailTransferPojoClass.setSentTime(LocalDateTime.now());
                mailTransferRepo.save(mailTransferPojoClass);

                String notificationOfFeedback = "User:" + feedbackObject.get("senderEmail") + " had sent an FeedBack:" + feedbackObject.get("message");
                String toEmail = "akshatshrivastav1591@gmail.com";
                String subject = "Notification of Feedback";
                if (sendEmail(toEmail, subject, notificationOfFeedback)) {

                    return ResponseEntity.ok(Map.of("message", "Feedback email sent:"));
                } else {
                    throw new RuntimeException("Something went wrong with sendEmail");

                }

            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Something went wrong with server."));
        }
    }


    public ResponseEntity<Map<String, String>> reportAnIssue(Map<String, String> reportingObject) {
        try {
            if (sendEmail(reportingObject.get("senderEmail"), reportingAnIssuesSubject, reportingAnIssueReturnMessageBody)) {
                MailTransferPojoClass mailTransferPojoClass = new MailTransferPojoClass();
                mailTransferPojoClass.setSubject(reportingAnIssuesSubject);
                mailTransferPojoClass.setBody(reportingObject.get("message"));
                mailTransferPojoClass.setSenderEmailId(reportingObject.get("senderEmail"));
                mailTransferPojoClass.setSentTime(LocalDateTime.now());
                mailTransferRepo.save(mailTransferPojoClass);
                String notificationOFReportingAnIssue = "User:" + reportingObject.get("senderEmail") + " had report an issue:" + reportingObject.get("message");
                String toEmail = "akshatshrivastav1591@gmail.com";
                String subject = "Notification of ReportingOfAnIssue";
                if (sendEmail(toEmail, subject, notificationOFReportingAnIssue)) {
                    return ResponseEntity.ok(Map.of("message", "issue reported"));
                } else {
                   throw new RuntimeException("Something went wrong with sending  mail");
                }

            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Something went wrong with server."));

        }
    }
}
