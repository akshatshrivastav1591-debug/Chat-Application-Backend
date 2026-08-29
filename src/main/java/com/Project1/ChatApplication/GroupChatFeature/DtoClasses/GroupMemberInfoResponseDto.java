package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberInfoResponseDto {

    private  String userID;
    private String userProfilePicture;
    private String contactSavedName;
    private String lastCheckedMessageID;
    private boolean Admin;
}
