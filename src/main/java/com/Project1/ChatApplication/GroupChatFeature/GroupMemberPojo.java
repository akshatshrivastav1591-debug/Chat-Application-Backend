package com.Project1.ChatApplication.GroupChatFeature;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long index;
    private String userId;
    private boolean admin;
    private LocalDateTime joiningDate;
    private String groupId;
    @Column(name = "lastcheckedmessageid")
    private String lastCheckedMessageID;
    @Column(name = "uncheckedMessagecounter")
    private  int uncheckedMessageCounter;
}
