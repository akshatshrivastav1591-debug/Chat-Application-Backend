package com.Project1.ChatApplication.UserContacts;

import com.Project1.ChatApplication.RoomID.RoomIdService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    RoomIdService roomIdService;
    @Autowired
    USerIdUtilMethods uSerIdUtilMethods;

    //-->Add New Contact
    public ResponseEntity<?> saveNewContact(Map<String, String> userContacts) {

        try {
            Map<String, Object> result = contactsUtil.authenticateContactNO(userContacts.get("contactNumber"));
            if (!(boolean) result.get("status")) {

                return ResponseEntity.status(401).body(Map.of("message", result.get("Response").toString()));
            }
            if (newContact.existsBySavedByAndSavedUserID(uSerIdUtilMethods.extractUserIdFromJwtToken(), result.get("Response").toString())) {
                return ResponseEntity.status(401).body(Map.of("message", "User Already Added in the contacts:"));
            }
            if (result.get("Response").toString().equals(savedByUserId.extractUserIdFromJwtToken())) {
                return ResponseEntity.status(401).body(Map.of("message", "You can't add yourSelf"));
            }
            UserContactPojo userContact = new UserContactPojo();
            userContact.setSavedBy(savedByUserId.extractUserIdFromJwtToken());
            userContact.setSavedUserID(result.get("Response").toString());
            userContact.setSavedName(userContacts.get("savedName"));
            if (!roomIdService.roomIDGeneration(userContact.getSavedBy(), userContact.getSavedUserID()))
                return ResponseEntity.status(500).body("Something Went Wrong,RoomID not created:");
            newContact.save(userContact);
            return ResponseEntity.ok(Map.of("message", "Success,New Contact Added Successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Something went wrong with server:"));
        }
    }

    // Get all contacts
    public ResponseEntity<?> getAllContacts() {
        try {
            ArrayList<UserContactPojo> contactsList = newContact.findBySavedBy(savedByUserId.extractUserIdFromJwtToken());
            ArrayList<UserContactDtoClass> listOfDtoObjects = new ArrayList<>();
            for (UserContactPojo userContacts : contactsList) {
                UserContactDtoClass tempDto = new UserContactDtoClass();
                tempDto.setSavedUserContactNo(savedByUserId.getUserMobileNo(userContacts.getSavedUserID()));
                tempDto.setProfilePhotoUrl(profileService.getProfileImage(userContacts.getSavedUserID()));
                tempDto.setRoomId(roomIdService.getRoomID(savedByUserId.extractUserIdFromJwtToken(), userContacts.getSavedUserID()));
                if (userContacts.getSavedName() == null || userContacts.getSavedName().isBlank()) {
                    tempDto.setSavedName(profileService.getMobileNo(userContacts.getSavedUserID()));
                } else {
                    tempDto.setSavedName(userContacts.getSavedName());
                }
                tempDto.setContactUserId(userContacts.getSavedUserID());
                listOfDtoObjects.add(tempDto);
            }

            return ResponseEntity.ok(Map.of("contactLists", listOfDtoObjects));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Something went wrong:"));
        }
    }

    //-->Update Old contacts
    public ResponseEntity<?> updateContact(Map<String, String> updatedUserDetails) {
        try {
            Map<String, Object> oldUserId = contactsUtil.authenticateContactNO(updatedUserDetails.get("oldContactNumber"));
            if (!(boolean) oldUserId.get("status"))
                return ResponseEntity.status(401).body(Map.of("message", oldUserId.get("Response")));
            else {
                UserContactPojo oldUserDetails = newContact.findBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), oldUserId.get("Response").toString());
                if (oldUserDetails == null)
                    return ResponseEntity.status(401).body(Map.of("message", "This Contact not exist in your contacts list:"));
                Map<String, Object> updatedUserId = contactsUtil.authenticateContactNO(updatedUserDetails.get("updatedContactNumber"));
                if (!(boolean) updatedUserId.get("status"))
                    return ResponseEntity.status(401).body(Map.of("message", updatedUserId.get("Response").toString()));

                if (oldUserId.get("Response").equals(updatedUserId.get("Response"))) {
                    oldUserDetails.setSavedName(updatedUserDetails.get("updatedSavedName"));
                    newContact.save(oldUserDetails);
                    return ResponseEntity.ok(Map.of("message", "User Named Changed Successfully:"));
                }
                if (newContact.existsBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), updatedUserId.get("Response").toString())) {
                    return ResponseEntity.status(401).body(Map.of("message", "This Number already exists in your contact:"));
                }

                oldUserDetails.setSavedUserID(updatedUserId.get("Response").toString());
                oldUserDetails.setSavedName(updatedUserDetails.get("updatedSavedName"));
                newContact.save(oldUserDetails);
                return ResponseEntity.ok(Map.of("message", "Successfully updated the Contact:"));

            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Something went wrong:"));
        }
    }

    //-->Delete Contact
    public ResponseEntity<?> deleteContact(String deleteContactNo) {
        try {
        Map<String, Object> deletingContactId = contactsUtil.authenticateContactNO(deleteContactNo);
        if (!(boolean) deletingContactId.get("status"))
            return ResponseEntity.status(401).body(Map.of("message", deletingContactId.get("Response")));
        if (!newContact.existsBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), deletingContactId.get("Response").toString()))
            return ResponseEntity.status(401).body(Map.of("message", "Contact not exists:"));
        UserContactPojo deletingContactDetails = newContact.findBySavedByAndSavedUserID(savedByUserId.extractUserIdFromJwtToken(), deletingContactId.get("Response").toString());
        newContact.delete(deletingContactDetails);
        return ResponseEntity.ok(Map.of("message", "Successfully Deleted the Contact:"));
    }catch (Exception e){
            return ResponseEntity.ok(Map.of("message", "Something went wrong:"));
        }
}

//-->Get Saved Name;
    public String getSavedName(String UserID1,String UserID2){
        UserContactPojo userContactPojo=newContact.findBySavedByAndSavedUserID(UserID1,UserID2);
        if(userContactPojo==null) return null;
        return  userContactPojo.getSavedName();
    }
//Get All contacts
    public  List<UserContactPojo> getAllContactsForGroup(String userId){
        List <UserContactPojo> fetchedContacts=newContact.findBySavedBy(userId);
        if(fetchedContacts.isEmpty()) return  null;
        return  fetchedContacts;
    }
 public Map<String,Object> savingNewContactOnReceiverSide(String savedBy,String saved){
        try {


            //--> This method save a contact when user receive message for the first time from other user:
            if (newContact.existsBySavedByAndSavedUserID(savedBy, saved)) return Map.of("IsFirstChat",false,"IsGood",true);
            else {
                UserContactPojo userContactPojo = new UserContactPojo();
                userContactPojo.setSavedBy(savedBy);
                userContactPojo.setSavedUserID(saved);
                userContactPojo.setSavedName(savedByUserId.getUserMobileNo(saved));
                newContact.save(userContactPojo);

                return Map.of("IsFirstChat",true,"IsGood",true);
            }
        }catch (Exception e){

            return Map.of("IsFirstChat","","IsGood",false);
        }
 }


}

