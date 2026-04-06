package com.Project1.ChatApplication.UserContacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserContactsController {
    @Autowired
    UserContactsService contactsService;
    @PostMapping("/addNewContact")
    public ResponseEntity<?> saveContact(@RequestBody Map<String,String>userContacts){
        return contactsService.saveNewContact(userContacts);
    }
@GetMapping("/getAllContacts")
public ResponseEntity<?> getAllContacts(){
        return contactsService.getAllContacts();
    }
@PutMapping("/updateContact")
public ResponseEntity<?> updateContact(@RequestBody Map<String,String> updatedUserDetails){
        return  contactsService.updateContact(updatedUserDetails);
    }
@DeleteMapping("/deleteContact")
public ResponseEntity<?> deleteContact(@RequestBody String deleteContactNo){
        return contactsService.deleteContact(deleteContactNo);
}
}


