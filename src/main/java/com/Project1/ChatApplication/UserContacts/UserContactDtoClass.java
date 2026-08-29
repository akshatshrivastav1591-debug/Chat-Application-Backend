package com.Project1.ChatApplication.UserContacts;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContactDtoClass {
    private String profilePhotoUrl;
    private  String savedName;
    private Long roomId;
     private String savedUserContactNo;
     private String contactUserId;

}
