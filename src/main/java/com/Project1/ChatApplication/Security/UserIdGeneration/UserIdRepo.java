package com.Project1.ChatApplication.Security.UserIdGeneration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIdRepo extends JpaRepository<UserIdPojoClass,String> {
    UserIdPojoClass findByMobileNo(String mobileno);
    UserIdPojoClass findByexternalUserID(String externalUserID);
    boolean existsByMobileNo(String mobileNO);
}
