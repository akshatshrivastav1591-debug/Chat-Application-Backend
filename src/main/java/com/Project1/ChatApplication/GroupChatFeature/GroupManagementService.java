package com.Project1.ChatApplication.GroupChatFeature;


import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupInfoResponseDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupMemberInfoResponseDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.RequestDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.UpdatedGroupInfoDto;
import com.Project1.ChatApplication.RoomID.RoomIdService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;

import com.Project1.ChatApplication.UserContacts.UserContactDtoClass;
import com.Project1.ChatApplication.UserContacts.UserContactPojo;
import com.Project1.ChatApplication.UserContacts.UserContactsService;
import com.Project1.ChatApplication.UserProfile.Cloudinary.CloudinaryServiceImple;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import com.Project1.ChatApplication.WrapperClasses.ApiWrapperClass;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class GroupManagementService {
    @Autowired
    private GroupInfoRepo groupInfoRepo;
    @Autowired
    private USerIdUtilMethods userIdUtilMethods;
    @Autowired
    private  RoomIdService roomIdService;
    @Autowired
    private  GroupMembersInfoRepo groupMembersInfoRepo;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserContactsService userContactsService;
    @Autowired
    CloudinaryServiceImple cloudinary;
    public ResponseEntity<ApiWrapperClass<Void>> createNewGroup(RequestDto groupInfo) {
        try {
            GroupInfoPojoClass newGroup = new GroupInfoPojoClass();
            newGroup.setGroupName(groupInfo.getGroupName());
            newGroup.setGroupIconUrl(groupInfo.getGroupLogoUrl());
            newGroup.setGroupIconPublicId(groupInfo.getGroupLogoPublicId());
            newGroup.setGroupIconType(groupInfo.getGroupLogoType());
            newGroup.setCreationTime(LocalDateTime.now());
            newGroup.setCreatorUserID(userIdUtilMethods.extractUserIdFromJwtToken());
            newGroup.setLastMessage("No recent Message:");
            newGroup.setLastMessageSendingTime(LocalTime.now());
            newGroup.setLastMessageSendingDate(LocalDate.now());
            newGroup.setAdminNumbers(1);

            List<Long> membersList =  groupInfo.getGroupMembers();
            newGroup.setGroupMembers((long) (membersList.size() + 1));
            groupInfoRepo.save(newGroup);
            if(membersList.isEmpty()){
                GroupMemberPojo admin = new GroupMemberPojo();
                admin.setUserId(userIdUtilMethods.extractUserIdFromJwtToken());
                admin.setGroupId(newGroup.getGroupId());
                admin.setJoiningDate(LocalDateTime.now());
                admin.setAdmin(true);
                groupMembersInfoRepo.save(admin);
            }
            else {
                for (Long roomId : membersList) {
                    GroupMemberPojo groupMemberPojo = new GroupMemberPojo();
                    groupMemberPojo.setGroupId(newGroup.getGroupId());
                    groupMemberPojo.setAdmin(false);
                    groupMemberPojo.setJoiningDate(LocalDateTime.now());
                    groupMemberPojo.setUserId(roomIdService.getRecieverID(roomId, userIdUtilMethods.extractUserIdFromJwtToken()));
                    groupMembersInfoRepo.save(groupMemberPojo);
                }
                GroupMemberPojo admin = new GroupMemberPojo();
                admin.setUserId(userIdUtilMethods.extractUserIdFromJwtToken());
                admin.setGroupId(newGroup.getGroupId());
                admin.setJoiningDate(LocalDateTime.now());
                admin.setAdmin(true);
                groupMembersInfoRepo.save(admin);
            }
            return ResponseEntity.ok(new ApiWrapperClass<>(null,true,"GroupCreated"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong with Server:Reason:"+e.getLocalizedMessage()));
        }
    }

    public ResponseEntity<ApiWrapperClass<List<GroupInfoResponseDto>>> getAllGroupsDetails() {
        try {
            String currentUserID = userIdUtilMethods.extractUserIdFromJwtToken();
            List<GroupMemberPojo> groupIDs = groupMembersInfoRepo.findByUserId(currentUserID);
            if (groupIDs.isEmpty())
                return ResponseEntity.ok(new ApiWrapperClass<>(null, true, "No Groups are available:"));
            List<GroupInfoResponseDto> listOFResponseDto = new ArrayList<>();
            for (GroupMemberPojo groupID : groupIDs) {
                GroupInfoPojoClass groupInfo = groupInfoRepo.findByGroupId(groupID.getGroupId());
                GroupInfoResponseDto responseDto = getGroupInfoResponseDto(groupInfo);
                responseDto.setUnseenMessageCounter(groupID.getUncheckedMessageCounter());
                responseDto.setAdmin(groupID.isAdmin());
                responseDto.setLastCheckedMessageID(groupID.getLastCheckedMessageID());
                responseDto.setUserContactInGroup(groupMembersInfoRepo.contactInGroup(userIdUtilMethods.extractUserIdFromJwtToken(),groupID.getGroupId()));
                listOFResponseDto.add(responseDto);
            }
            listOFResponseDto.sort(
                    Comparator.comparing(GroupInfoResponseDto::getLastMessageSendingDate).reversed()
                            .thenComparing(Comparator.comparing(GroupInfoResponseDto::getLastMessageSendingTime).reversed())
            );
            return ResponseEntity.ok(new ApiWrapperClass<>(listOFResponseDto, true, "Group Info List"));
        }catch (Exception e){
            return  ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong with Server"+e.getLocalizedMessage()));
        }
    }
    private static @NonNull GroupInfoResponseDto getGroupInfoResponseDto(GroupInfoPojoClass groupInfo) {
        GroupInfoResponseDto responseDto=new GroupInfoResponseDto();
        responseDto.setGroupId(groupInfo.getGroupId());
        responseDto.setGroupName(groupInfo.getGroupName());
        responseDto.setGroupLogoUrl(groupInfo.getGroupIconUrl());
        responseDto.setGroupTotalMembers(groupInfo.getGroupMembers());
        responseDto.setLastMessage(groupInfo.getLastMessage());
        responseDto.setLastMessageSendingTime(groupInfo.getLastMessageSendingTime());
        responseDto.setLastMessageSendingDate(groupInfo.getLastMessageSendingDate());

        return responseDto;
    }

    public ResponseEntity<ApiWrapperClass<Void>> deleteGroupMember(RequestDto deletingGroupID) {
        try {


            GroupMemberPojo deletedMember = groupMembersInfoRepo.findByUserIdAndGroupId(userIdUtilMethods.extractUserIdFromJwtToken(), deletingGroupID.getGroupID());
            GroupInfoPojoClass deletingGroupInfo = groupInfoRepo.findByGroupId(deletingGroupID.getGroupID());
            if (deletedMember.isAdmin()) deletingGroupInfo.setAdminNumbers(deletingGroupInfo.getAdminNumbers() - 1);
            deletingGroupInfo.setGroupMembers((deletingGroupInfo.getGroupMembers() - 1));
            groupInfoRepo.save(deletingGroupInfo);
            groupMembersInfoRepo.delete(deletedMember);
            return ResponseEntity.ok(new ApiWrapperClass<>(null, true, "user Successfully Deleted from the group:"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong with Server:"));
        }
    }

    public ResponseEntity<ApiWrapperClass<List<GroupMemberInfoResponseDto>>> getAllGroupMembers(String groupID) {
        try {
          List<GroupMemberPojo> groupMemberList=groupMembersInfoRepo.findByGroupId(groupID);
          List<GroupMemberInfoResponseDto> responseDtoList=new ArrayList<>();
             for(GroupMemberPojo member:groupMemberList){
                 if(!member.getUserId().equals(userIdUtilMethods.extractUserIdFromJwtToken())){
                 GroupMemberInfoResponseDto responseDto=new GroupMemberInfoResponseDto();
                 responseDto.setUserID(member.getUserId());
                 responseDto.setUserProfilePicture(userProfileService.getProfileImage(member.getUserId()));
                 responseDto.setAdmin(member.isAdmin());
                 String userName= userContactsService.getSavedName(userIdUtilMethods.extractUserIdFromJwtToken(), member.getUserId());
                 if(userName==null) responseDto.setContactSavedName(userIdUtilMethods.getUserMobileNo(member.getUserId()));
                 else responseDto.setContactSavedName(userName);
                 responseDtoList.add(responseDto);
                 }
             }
             return  ResponseEntity.ok(new ApiWrapperClass<>(responseDtoList,true,"Group Members Info fetched successfully:"));
        }catch (Exception e){
            return  ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong with Server:"));
        }
    }


    public ResponseEntity<ApiWrapperClass<Void>> updateGroupInfo(UpdatedGroupInfoDto updatedGroupInfo) {
        try {


            //-->Updating the Information of the Group
            GroupInfoPojoClass groupInfoPojoClass = groupInfoRepo.findByGroupId(updatedGroupInfo.getGroupID());
            if (updatedGroupInfo.getUpdatedGroupLogoUrl() != null) {
                if(groupInfoPojoClass.getGroupIconPublicId()==null){
                    groupInfoPojoClass.setGroupIconUrl(updatedGroupInfo.getUpdatedGroupLogoUrl());
                    groupInfoPojoClass.setGroupIconPublicId(updatedGroupInfo.getUpdatedGroupLogoPublicId());
                    groupInfoPojoClass.setGroupIconType(updatedGroupInfo.getUpdatedGroupLogoType());
                }
                else {
                    boolean cloudinaryResponse = cloudinary.deleteMultipartFile(groupInfoPojoClass.getGroupIconPublicId(), groupInfoPojoClass.getGroupIconType());
                    if (cloudinaryResponse) {
                        groupInfoPojoClass.setGroupIconUrl(updatedGroupInfo.getUpdatedGroupLogoUrl());
                        groupInfoPojoClass.setGroupIconPublicId(updatedGroupInfo.getUpdatedGroupLogoPublicId());
                        groupInfoPojoClass.setGroupIconType(updatedGroupInfo.getUpdatedGroupLogoType());
                    } else {
                        throw new IOException("CloudinaryException");

                    }
                }
                groupInfoRepo.save(groupInfoPojoClass);
            }
              if(updatedGroupInfo.getUpdatedName()!=null&&!updatedGroupInfo.getUpdatedName().isEmpty()){
                  groupInfoPojoClass.setGroupName(updatedGroupInfo.getUpdatedName());
              }

            groupInfoPojoClass.setGroupMembers((groupInfoPojoClass.getGroupMembers() - updatedGroupInfo.getRemovedMember().size()) + updatedGroupInfo.getNewMembers().size());
            groupInfoPojoClass.setAdminNumbers(groupInfoPojoClass.getAdminNumbers() + updatedGroupInfo.getNewAdmins().size());
            //updating the groupInfo if all the list are empty
            if(updatedGroupInfo.getNewMembers().isEmpty()&&updatedGroupInfo.getNewAdmins().isEmpty()&&updatedGroupInfo.getRemovedMember().isEmpty()) {
                groupInfoRepo.save(groupInfoPojoClass);
            }

            //-->Adding New Members to the Group:
            if (!updatedGroupInfo.getNewMembers().isEmpty()) {
                for (UserContactDtoClass newMember : updatedGroupInfo.getNewMembers()) {
                    GroupMemberPojo groupMemberPojo = new GroupMemberPojo();
                    groupMemberPojo.setUserId(newMember.getContactUserId());
                    groupMemberPojo.setAdmin(false);
                    groupMemberPojo.setGroupId(updatedGroupInfo.getGroupID());
                    groupMemberPojo.setJoiningDate(LocalDateTime.now());
                    groupMembersInfoRepo.save(groupMemberPojo);
                }
            }
            //-->Updating the list of new Admins
            if (!updatedGroupInfo.getNewAdmins().isEmpty()) {
                for (GroupMemberInfoResponseDto newAdmin : updatedGroupInfo.getNewAdmins()) {
                    GroupMemberPojo fetchedGroupMember = groupMembersInfoRepo.findByUserIdAndGroupId(newAdmin.getUserID(), updatedGroupInfo.getGroupID());
                    fetchedGroupMember.setAdmin(true);
                    groupMembersInfoRepo.save(fetchedGroupMember);
                }
            }
            //-->Removing the Members of the group
            if (!updatedGroupInfo.getRemovedMember().isEmpty()) {
                for (GroupMemberInfoResponseDto deletedMember : updatedGroupInfo.getRemovedMember()) {
                    GroupMemberPojo deletedGroupMember = groupMembersInfoRepo.findByUserIdAndGroupId(deletedMember.getUserID(), updatedGroupInfo.getGroupID());
                    groupMembersInfoRepo.delete(deletedGroupMember);
                }
            }


            return ResponseEntity.ok(new ApiWrapperClass<>(null, true, "GroupInfoUpdatedSuccessfully:"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null, false, "Something went wrong,Reason:" + e.getLocalizedMessage()));
        }

    }

    public boolean updateLastCheckedMethodForSomeMembers(String groupID,String messageID,String userID){
        try {
            int result = groupMembersInfoRepo.updatingLastCheckedMessage(userID, groupID, messageID);
                return result > 0;
        }catch (Exception e){
            return false;
        }

    }
    public boolean  updateRecentMessageInGroupInfo(String message, LocalDate sendingDate, LocalTime sendingTime,String groupID){
        try {
            int row=groupInfoRepo.updatingRecentMessages(message,groupID,sendingTime,sendingDate);
            if(row>0) return true;
            throw new RuntimeException("Row not updated:");
        }catch (Exception e){
            return false;
        }
    }
    //updating uncheckedMessage Counter for simple messages
    public boolean  updatingUncheckedMessageCounter(String lastMessageId, String groupID){
        try {
            int row=groupMembersInfoRepo.updatingUnseenMessageCount(groupID,lastMessageId);
            if(row>0) return true;
            throw new RuntimeException("Row not updated:");
        }catch (Exception e){
            return  false;
        }
    }

    //updating uncheckedMessage Counter for multipartRequest
    public boolean  updatingUncheckedMessageCounterForMultipartRequest(String lastMessageId, String groupID,int uncheckedMessageLength){
        try {
            int row=groupMembersInfoRepo.updatingUnseenMessageCountForMultipartMessage(groupID,lastMessageId,uncheckedMessageLength);
            if(row>0) return true;
            throw new RuntimeException("Row not updated:");
        }catch (Exception e){
            return  false;
        }
    }
 //updating the uncheckedCounter to 0;
 public boolean  updatingUncheckedMessageCounterTo0(String userId, String groupID){
     try {
         int row= groupMembersInfoRepo.updatingUnseenMessageCountTo0(groupID,userId);
         if(row>0) return true;
         throw new RuntimeException("Row not updated:");
     }catch (Exception e){

         return  false;
     }
 }
}
