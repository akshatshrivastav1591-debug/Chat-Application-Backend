package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;

import com.Project1.ChatApplication.UserContacts.UserContactPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoResponseDto {
private String groupName;
private String groupId;
private String lastCheckedMessageID;
private String lastMessage;
private String groupLogoUrl;
private Long   groupTotalMembers;
private LocalTime lastMessageSendingTime;
private LocalDate lastMessageSendingDate;
private boolean admin;
private  int unseenMessageCounter;
private List<UserContactPojo> userContactInGroup;
}
