package com.Project1.ChatApplication.GroupChatFeature;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoPojoClass {
@Id
private String groupId;
private String groupName;
private String groupIconUrl;
private Long groupMembers;
private String creatorUserID;
private LocalDateTime creationTime;
private String lastMessage;
private int adminNumbers;
private String groupIconPublicId;
private  String groupIconType;
@Column(name = "lastmessagesendingtime")
private LocalTime lastMessageSendingTime;
@Column(name = "lastmessagesendingdate")
private LocalDate lastMessageSendingDate;




@PrePersist
public  void generateGroupID() {
    this.groupId = UUID.randomUUID().toString();


}}