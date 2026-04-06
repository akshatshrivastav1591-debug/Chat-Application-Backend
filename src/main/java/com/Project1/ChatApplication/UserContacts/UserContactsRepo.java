package com.Project1.ChatApplication.UserContacts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Map;

public interface UserContactsRepo extends JpaRepository<UserContactPojo,Integer> {
  boolean existsBySavedUserID(String savedUserId);
   ArrayList<UserContactPojo> findAllBYSavedBy(String savedBYUserID);
    UserContactPojo findBySavedByAndSavedUserID(String savedBY,String savedUSerID);
    boolean existsBySavedByAndSavedUserID(String savedBY,String savedUSerID);

}
