package com.Project1.ChatApplication.UserProfile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepo extends JpaRepository<UserProfilePojoClass,String> {
    UserProfilePojoClass findByUserID(String userID);

}
