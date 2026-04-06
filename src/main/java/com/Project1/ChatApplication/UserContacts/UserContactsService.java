package com.Project1.ChatApplication.UserContacts;

import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
@Service
public class UserContactsService {
    @Autowired
    private UserContactsUtil contactsUtil;
    @Autowired
    private USerIdUtilMethods savedByUserId;
    @Autowired
    UserContactsRepo newContact;

    @Autowired
    UserProfileService profileService;

//-->Add New Contact
    public ResponseEntity<?> saveNewContact(Map<String, String> userContacts) {
        Map<String, Object> result = contactsUtil.authenticateContactNO(userContacts.get("contactNumber"));
        if (!(boolean) result.get("status")) {
            return ResponseEntity.status(401).body(result.get("Response").toString());
        }
        if (newContact.existsBySavedUserID(result.get("Response").toString())) {
            return ResponseEntity.status(401).body("User Already Added in the contacts:");
        }
        if (result.get("Response").toString().equals(savedByUserId.extractUserIdFromJwtToken())) {
            return ResponseEntity.status(401).body("You can't add yourSelf");
        }
        UserContactPojo userContact = new UserContactPojo();
        userContact.setSavedBy(savedByUserId.extractUserIdFromJwtToken());
        userContact.setSavedUserID(result.get("Response").toString());
        userContact.setSavedName(userContacts.get("savedName"));
        newContact.save(userContact);
        return ResponseEntity.ok("Success,New Contact Added Successfully");
    }
// Get all contacts
    public ResponseEntity<?> getAllContacts() {
        ArrayList<UserContactPojo> contactsList = newContact.findAllBYSavedBy(savedByUserId.extractUserIdFromJwtToken());
        ArrayList<UserContactDtoClass> listOfDtoObjects = new ArrayList<>();
        for (UserContactPojo userContacts : contactsList) {
            UserContactDtoClass tempDto = new UserContactDtoClass();
            tempDto.setProfilePhotoUrl(profileService.getProfileImage(userContacts.getSavedUserID()));
            if (userContacts.getSavedName() == null || userContacts.getSavedName().isBlank()) {
                tempDto.setSavedName(profileService.getMobileNo(userContacts.getSavedUserID()));
            } else {
                tempDto.setSavedName(userContacts.getSavedName());
            }

            listOfDtoObjects.add(tempDto);
        }

        return ResponseEntity.ok(listOfDtoObjects);
    }
//-->Update Old contacts
    public ResponseEntity<?> updateContact(Map<String, String> updatedUserDetails) {
        Map<String, Object> oldUserId = contactsUtil.authenticateContactNO(updatedUserDetails.get("oldContactNumber"));
        if (!(boolean) oldUserId.get("status")) return ResponseEntity.status(401).body(oldUserId.get("Response"));
        else {
            UserContactPojo oldUserDetails = newContact.findBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), oldUserId.get("Response").toString());
            if (oldUserDetails == null)
                return ResponseEntity.status(401).body("This Contact not exist in your contacts list:");
            Map<String, Object> updatedUserId = contactsUtil.authenticateContactNO(updatedUserDetails.get("updatedContactNumber"));
            if (!(boolean) updatedUserId.get("status"))
                return ResponseEntity.status(401).body(updatedUserId.get("Response").toString());

            if(oldUserId.get("Response").equals(updatedUserId.get("Response"))) {
                oldUserDetails.setSavedName(updatedUserDetails.get("updatedSavedName"));
                newContact.save(oldUserDetails);
                return ResponseEntity.ok("User Named Changed Successfully:");
            }
            if(newContact.existsBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), updatedUserId.get("Response").toString()))
            { return ResponseEntity.status(401).body("This Number already exists in your contact:");}

                oldUserDetails.setSavedUserID(updatedUserId.get("Response").toString());
                oldUserDetails.setSavedName(updatedUserDetails.get("updatedSavedName"));
                newContact.save(oldUserDetails);
                return ResponseEntity.ok("Successfully updated the Contact:");

        }
    }
//-->Delete Contact
    public ResponseEntity<?> deleteContact(String deleteContactNo) {
        Map<String,Object> deletingContactId=contactsUtil.authenticateContactNO(deleteContactNo);
        if(!(boolean)deletingContactId.get("status")) return ResponseEntity.status(401).body(deletingContactId.get("Response"));
        if(!newContact.existsBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), deletingContactId.get("Response").toString()))
            return ResponseEntity.status(401).body("Contact not exists:");
        UserContactPojo deletingContactDetails=newContact.findBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), deletingContactId.get("Response").toString());
        newContact.delete(deletingContactDetails);
        return ResponseEntity.ok("Successfully Deleted the Contact:");
    }
}
