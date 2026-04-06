package com.Project1.ChatApplication.Security.UserIdGeneration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdPojoClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalUserID;
    @Column(unique = true)
    private  String mobileNo;
    @Column(unique = true)
    private  String externalUserID;
private  boolean firstlogin;
  @PrePersist
    public void generateExternalUSerID(){
      this.externalUserID= UUID.randomUUID().toString();
  }

}


