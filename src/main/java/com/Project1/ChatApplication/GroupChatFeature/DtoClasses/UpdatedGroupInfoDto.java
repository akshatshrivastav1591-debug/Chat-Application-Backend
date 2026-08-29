package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;

import com.Project1.ChatApplication.UserContacts.UserContactDtoClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedGroupInfoDto {
    private String groupID;
    private  List<UserContactDtoClass> newMembers;
    private  List<GroupMemberInfoResponseDto> newAdmins;
    private  List<GroupMemberInfoResponseDto> removedMember;
    private  String updatedName;
    private  String updatedGroupLogoUrl;
    private  String updatedGroupLogoPublicId;
    private  String updatedGroupLogoType;
}
