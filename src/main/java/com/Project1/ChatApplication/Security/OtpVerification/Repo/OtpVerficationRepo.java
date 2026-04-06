package com.Project1.ChatApplication.Security.OtpVerification.Repo;

import com.Project1.ChatApplication.Security.OtpVerification.UserOtpVerificationPojo.OtpVerificationPojoClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OtpVerficationRepo extends JpaRepository<OtpVerificationPojoClass,Integer> {
    OtpVerificationPojoClass findBymobileno(String mobileno);
    @Transactional
    @Modifying
    void deleteByexpired(boolean expired);
}
