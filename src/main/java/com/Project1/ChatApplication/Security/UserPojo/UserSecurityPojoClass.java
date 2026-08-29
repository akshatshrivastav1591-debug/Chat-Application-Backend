package com.Project1.ChatApplication.Security.UserPojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
public class UserSecurityPojoClass {
    @Id
    private  String mobileno;
    private  String password;

}
