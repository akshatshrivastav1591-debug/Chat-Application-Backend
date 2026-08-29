package com.Project1.ChatApplication.UserProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserProfilePojoClass {
    @Id
    private String userID;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String address;
    private String imageUrl;
    @Column(name = "imagepublicid")
    private String imagePublicId;
    @Column(name ="imagetype" )
    private String imageType;


}


