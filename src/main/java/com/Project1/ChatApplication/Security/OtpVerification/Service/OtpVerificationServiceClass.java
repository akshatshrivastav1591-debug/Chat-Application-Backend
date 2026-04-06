package com.Project1.ChatApplication.Security.OtpVerification.Service;
import com.Project1.ChatApplication.Security.OtpVerification.OtpVerificationUtilClass;
import com.Project1.ChatApplication.Security.OtpVerification.Repo.OtpVerficationRepo;
import com.Project1.ChatApplication.Security.OtpVerification.TwilioConfig.TwilioConfig;
import com.Project1.ChatApplication.Security.OtpVerification.UserOtpVerificationPojo.OtpVerificationPojoClass;
import com.Project1.ChatApplication.Security.PhoneUtil.PhoneNumberFormatter;
import com.Project1.ChatApplication.Security.SecurityRepo.UserSecurityRepo;
import com.Project1.ChatApplication.Security.SecurityService.NewUserService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import java.time.LocalDateTime;
import java.util.Map;
@Service
public class OtpVerificationServiceClass {
    @Autowired
    private UserSecurityRepo repo;
    @Autowired
    private OtpVerficationRepo otpRepo;
    @Autowired
    private TwilioConfig twilioConfig;
    @Autowired
     private NewUserService updatepassword;

    PhoneNumberFormatter Formatter = new PhoneNumberFormatter();
    OtpVerificationUtilClass otp = new OtpVerificationUtilClass();


    public String otpGeneration(UserSecurityPojoClass phonenumber) {
        OtpVerificationPojoClass otpObject = new OtpVerificationPojoClass();
        String authorizedNumber = Formatter.authenticateMobileNumber(phonenumber);
        if (authorizedNumber.equals("please enter all the credentials:") || authorizedNumber.equals("Mobile Numbers Can't contains other values:") || authorizedNumber.equals("Invalid Phone Number"))
            return authorizedNumber;
        if (!repo.existsById(authorizedNumber)) return "MobileNo not exist:";
        else {
            String generatedOtp = otp.otp();
            otpObject.setOtp(generatedOtp);
            otpObject.setMobileno(authorizedNumber);
            otpObject.setExpired(false);
            otpObject.setExpiredtime(LocalDateTime.now().plusMinutes(15));
            otpRepo.save(otpObject);
            String otpMessage = "Dear User,This message is done to Scam you: Kindly send this Otp:" + generatedOtp + " to Scammer.Thanks,Regards Scammer:Akshat Shrivastava:\uD83E\uDD23";
            return otpTransfer(authorizedNumber, generatedOtp, otpMessage);

        }
    }

    public String otpTransfer(String mobileNumber, String otp, String otpMessage) {

        try {
            Message message = Message.creator(new PhoneNumber(mobileNumber) //--> Otp Recivers Mobileno
                            , new PhoneNumber(twilioConfig.getTrialNumber()) //-->Otp Sender Mobile no
                            , otpMessage) //--> Otp and message:
                    .create();
            System.out.println("Message Sid:" + message.getSid());
            return "Four Digit Otp code is sent to your mobileNo:" + mobileNumber + ",this otp is valid for only 15 minutes";
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return "Something went wrong:";
        }
    }

    public String otpValidation(Map<String, String> otpDetails) throws Exception {
        OtpVerificationPojoClass fetchedOtp = otpRepo.findBymobileno(otpDetails.get("mobileNo"));
        if (!fetchedOtp.isExpired() && fetchedOtp.getExpiredtime().isAfter(LocalDateTime.now()) && fetchedOtp.getOtp().equals(otpDetails.get("otp"))) {
            fetchedOtp.setExpired(true);
            otpRepo.save(fetchedOtp);
            String result= updatepassword.updatePassword(otpDetails);
            if(result.equals("Please enter a different Password than old one:") ||result.equals(" Entered Confirm Password is not matched")||result.equals("Please Enter All the Fields:")){
                fetchedOtp.setExpired(false);
                otpRepo.save(fetchedOtp);
                return result;
            }
            else return result;

        }
        if (!fetchedOtp.getOtp().equals(otpDetails.get("otp"))) return "Otp is incorrect,please enter correct otp";
        else {
            return "Otp is Expired,Please Generate a new one and try again:";
        }


    }
}







