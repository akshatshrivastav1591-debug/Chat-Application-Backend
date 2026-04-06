package com.Project1.ChatApplication.Security.SecurityRepo;

import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSecurityRepo extends JpaRepository<UserSecurityPojoClass,String> {
    UserSecurityPojoClass findBymobileno(String username);
}
