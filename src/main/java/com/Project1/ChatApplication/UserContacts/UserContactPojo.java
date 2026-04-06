package com.Project1.ChatApplication.UserContacts;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContactPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sno;
    private String savedBy;
    private String savedUserID;
    private String savedName;
}
