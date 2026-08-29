package com.Project1.ChatApplication.UserContacts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface UserContactsRepo extends JpaRepository<UserContactPojo,Integer> {

   ArrayList<UserContactPojo> findBySavedBy(String savedBYUserID);

    UserContactPojo findBySavedByAndSavedUserID(String savedBY,String savedUSerID);
    boolean existsBySavedByAndSavedUserID(String savedBY,String savedUSerID);


}
