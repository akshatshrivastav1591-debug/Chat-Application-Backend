package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private String groupName;
    private String groupLogoUrl;
    private String groupLogoType;
    private String groupLogoPublicId;
    private List<Long> groupMembers;
    private String groupID;

}
