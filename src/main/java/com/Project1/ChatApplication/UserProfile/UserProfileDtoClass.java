package com.Project1.ChatApplication.UserProfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDtoClass {
    private String imageUrl;
    private  String firstName;
    private  String lastName;
    private Date dateOfBirth;
    private  String address;

}
