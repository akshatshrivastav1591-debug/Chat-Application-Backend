package com.Project1.ChatApplication.GroupChatFeature;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupMessageDto;
import com.Project1.ChatApplication.MessageDao.CloudinaryServiceImplForMulitPartRequest;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import com.Project1.ChatApplication.WrapperClasses.ApiWrapperClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class GroupMessageServiceClass {
    @Autowired
    private GroupMessageRepo groupMessageRepo;
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    USerIdUtilMethods userIdUtilMethods;
    @Autowired
    GroupManagementService groupManagementService;
    @Autowired
    CloudinaryServiceImplForMulitPartRequest cloudinaryService;
    @Autowired
    DeletedMessageRecordRepo deletingMessageForReceiverRecorder;
    public ApiWrapperClass<GroupMessageDto> saveMessage(String messageContent, String groupId, String senderUserID,Set<String> currentlyActiveMembers) {

        try {
            GroupMessageInfoPojo groupMessageInfoPojo = getGroupMessagePojo(messageContent, groupId, senderUserID,false,null,null,null,null,null);
            for(String currentlyActiveGroupMemberId:currentlyActiveMembers){
                   boolean response= groupManagementService.updateLastCheckedMethodForSomeMembers(groupId,groupMessageInfoPojo.getMessageID(),currentlyActiveGroupMemberId);
                   if(!response) throw new RuntimeException("something wrong with method:updateLastCheckedMethodForSomeMembers");

            }
            if(!groupManagementService.updateRecentMessageInGroupInfo(groupMessageInfoPojo.getMessageContent(), groupMessageInfoPojo.getSendingDate(), groupMessageInfoPojo.getSendingTime(), groupId)) throw new RuntimeException("Something went wrong with updating recent message");
            if(!groupManagementService.updatingUncheckedMessageCounter(groupMessageInfoPojo.getMessageID(),groupId)) throw new RuntimeException("Something went wrong with updating uncheckedMessageCounter Method:");
            groupMessageRepo.save(groupMessageInfoPojo);
            GroupMessageDto groupMessageDto = getGroupMessageResponseDto(groupMessageInfoPojo,userProfileService.getProfileImage(senderUserID),userIdUtilMethods.getUserMobileNo(senderUserID));
            groupMessageDto.setSenderProfilePicture(userProfileService.getProfileImage(senderUserID));
            groupMessageDto.setSenderName(userIdUtilMethods.getUserMobileNo(senderUserID));

            return new ApiWrapperClass<>(groupMessageDto,true,"Message saved:");
        }catch (Exception e) {
            return  new ApiWrapperClass<>(null,false,"Something went wrong with server:");
        }
    }



    private static GroupMessageDto getGroupMessageResponseDto(GroupMessageInfoPojo currentMessage,String senderUserProfileURl,String senderName){
        GroupMessageDto currentMessageResponseDto=new GroupMessageDto();
        currentMessageResponseDto.setGroupID(currentMessage.getGroupID());
        currentMessageResponseDto.setMessageID(currentMessage.getMessageID());
        currentMessageResponseDto.setMessageContent(currentMessage.getMessageContent());
        currentMessageResponseDto.setSenderID(currentMessage.getSenderUserID());
        currentMessageResponseDto.setSendingDate(currentMessage.getSendingDate());
        currentMessageResponseDto.setSendingTime(currentMessage.getSendingTime());
        currentMessageResponseDto.setMultipartRequestFlag(currentMessage.isMultipartRequest());
        currentMessageResponseDto.setFileName(currentMessage.getFileName());
        currentMessageResponseDto.setFileSize(currentMessage.getFileSize());
        currentMessageResponseDto.setFileUrl(currentMessage.getFileUrl());
        currentMessageResponseDto.setFileType(currentMessage.getFileType());
        currentMessageResponseDto.setSenderProfilePicture(senderUserProfileURl);
        currentMessageResponseDto.setSenderName(senderName);
        return  currentMessageResponseDto;
    }
private  static  GroupMessageInfoPojo getGroupMessagePojo(String messageContent,String groupId,String senderUserID,boolean multiPartRequestFlag,String fileName,String fileUrl,String fileSize,String fileType,String filePublicID){
    GroupMessageInfoPojo groupMessageInfoPojo = new GroupMessageInfoPojo();
    groupMessageInfoPojo.setGroupID(groupId);
    groupMessageInfoPojo.setMessageContent(messageContent);
    groupMessageInfoPojo.setSendingDate(LocalDate.now());
    groupMessageInfoPojo.setSendingTime(LocalTime.now());
    groupMessageInfoPojo.setSenderUserID(senderUserID);
    groupMessageInfoPojo.setMultipartRequest(multiPartRequestFlag);
    groupMessageInfoPojo.setFileName(fileName);
    groupMessageInfoPojo.setFileSize(fileSize);
    groupMessageInfoPojo.setFileUrl(fileUrl);
    groupMessageInfoPojo.setFileType(fileType);
    groupMessageInfoPojo.setFilePublicID(filePublicID);
    groupMessageInfoPojo.setDeletedForSender(false);

    groupMessageInfoPojo.setSend(true);
    return groupMessageInfoPojo;
}
    public ApiWrapperClass<List<GroupMessageDto>> saveMultipartRequest(List<JsonNode> fileList, String groupId, String senderUserID,Set<String> currentlyActiveUsers) {
        try {
        List<GroupMessageInfoPojo> savedMultipartMessages = new ArrayList<>();
        List<GroupMessageDto> lisOfGroupMessageDto = new ArrayList<>();
        for (JsonNode singleFile : fileList) {
            GroupMessageInfoPojo singleMessage = getGroupMessagePojo(singleFile.get("attachedMessage").asString(), groupId, senderUserID, true, singleFile.get("fileName").asString(), singleFile.get("fileUrl").asString(), singleFile.get("fileSize").asString(), singleFile.get("fileType").asString(), singleFile.get("filePublicId").asString());
            groupMessageRepo.save(singleMessage);
            savedMultipartMessages.add(singleMessage);
        }

        for (String currentlyActiveGroupMemberId : currentlyActiveUsers) {
            GroupMessageInfoPojo lastMultipartPojo = savedMultipartMessages.getLast();
            boolean response = groupManagementService.updateLastCheckedMethodForSomeMembers(groupId, lastMultipartPojo.getMessageID(), currentlyActiveGroupMemberId);
            if (!response)
                throw new RuntimeException("something wrong with method:updateLastCheckedMethodForSomeMembers");
        }
        if (!groupManagementService.updateRecentMessageInGroupInfo(savedMultipartMessages.getLast().getFileType(), savedMultipartMessages.getLast().getSendingDate(), savedMultipartMessages.getLast().getSendingTime(), groupId))
            throw new RuntimeException("Something went wrong with the updating Recent message as multipart request:");
        if (!groupManagementService.updatingUncheckedMessageCounterForMultipartRequest(savedMultipartMessages.getLast().getMessageID(), groupId, savedMultipartMessages.size()))
            throw new RuntimeException("Something went wrong with the updating UncheckedMessageCounter method multipart request:");
        for (GroupMessageInfoPojo savedSingleMessage : savedMultipartMessages) {
            GroupMessageDto singleDto = getGroupMessageResponseDto(savedSingleMessage, userProfileService.getProfileImage(senderUserID), userIdUtilMethods.getUserMobileNo(senderUserID));
            lisOfGroupMessageDto.add(singleDto);
        }
        return new ApiWrapperClass<>(lisOfGroupMessageDto, true, "Photos and Videos are saved:");
    }catch (Exception e){
            return new ApiWrapperClass<>(null,false,"Something went wrong with server:");
        }
    }

    public ResponseEntity<ApiWrapperClass<List<GroupMessageDto>>> getGroupMessages(String groupId) {
        //fetching the first batch of Messages
        try {

            List<GroupMessageInfoPojo> groupMessagePojoList = groupMessageRepo.findByGroupId(groupId);
            if (groupMessagePojoList.isEmpty()) return ResponseEntity.ok(new ApiWrapperClass<>(null,true,"No Messages are Available til now in group:"));
            if(!groupManagementService.updateLastCheckedMethodForSomeMembers(groupId,groupMessagePojoList.getFirst().getMessageID(), userIdUtilMethods.extractUserIdFromJwtToken())) throw new RuntimeException("Something went wrong with the updating lastCheckedMessage");
            if(!groupManagementService.updatingUncheckedMessageCounterTo0(userIdUtilMethods.extractUserIdFromJwtToken(),groupId))  throw new RuntimeException("Something went wrong with the updating the unseen message counter to 0");
            Collections.reverse(groupMessagePojoList);
            List<GroupMessageDto> messageDtoList = new ArrayList<>();

            for (GroupMessageInfoPojo currentMessagePojo : groupMessagePojoList) {

                if((currentMessagePojo.getSenderUserID().equals(userIdUtilMethods.extractUserIdFromJwtToken())&&!currentMessagePojo.isDeletedForSender()) || (!currentMessagePojo.getSenderUserID().equals(userIdUtilMethods.extractUserIdFromJwtToken()) && !deletingMessageForReceiverRecorder.isThisMessageDeletedForThisReceiver(currentMessagePojo.getMessageID(), userIdUtilMethods.extractUserIdFromJwtToken()))) {
                    messageDtoList.add(getGroupMessageResponseDto(currentMessagePojo,userProfileService.getProfileImage(currentMessagePojo.getSenderUserID()),userIdUtilMethods.getUserMobileNo(currentMessagePojo.getSenderUserID())));
                }
            }
            return  ResponseEntity.ok(new ApiWrapperClass<>(messageDtoList,true,"Messages Successfully fetched:"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);

        }
    }

    public ResponseEntity<ApiWrapperClass<List<GroupMessageDto>>> getRemainingGroupMessage(String groupId, LocalDate lastMessageDate, LocalTime lastMessageTime) {
        //fetching the Remaining  messages:
        try {
            //fetching the remaining messages of last message date:
            List<GroupMessageInfoPojo> remainingMessagesList = groupMessageRepo.findByGroupIdAndSendingDateAndSendingTime(groupId, lastMessageDate, lastMessageTime);
            List<GroupMessageDto> groupMessageDtoList = new ArrayList<>();
            if (remainingMessagesList.isEmpty()) {
                //if the previous method returning null, we are fetching the next batch of messages from next date:
                List<GroupMessageInfoPojo> nextDateMessage = groupMessageRepo.fetchingForNextDate(groupId, lastMessageDate);
                if (nextDateMessage.isEmpty()) return ResponseEntity.status(204).body(null);
                else {
                    Collections.reverse(nextDateMessage);


                    for (GroupMessageInfoPojo  currentMessageObject : nextDateMessage) {
                        if((currentMessageObject.getSenderUserID().equals(userIdUtilMethods.extractUserIdFromJwtToken())&&!currentMessageObject.isDeletedForSender()) || (!currentMessageObject.getSenderUserID().equals(userIdUtilMethods.extractUserIdFromJwtToken()) && !deletingMessageForReceiverRecorder.isThisMessageDeletedForThisReceiver(currentMessageObject.getMessageID(), userIdUtilMethods.extractUserIdFromJwtToken()))) {
                            groupMessageDtoList.add(getGroupMessageResponseDto(currentMessageObject,userProfileService.getProfileImage(currentMessageObject.getSenderUserID()),userIdUtilMethods.getUserMobileNo(currentMessageObject.getSenderUserID())));
                        }
                    }
                    return ResponseEntity.ok(new ApiWrapperClass<>(groupMessageDtoList,true,"Remaining Group fetched successfully:"));
                }
            }
            //if the messageRepo.findByRoomIdAndSendingDateAndSendingTime(roomId, lastMessageDate, lastMessageTime); method is returning not null List ,but the
            //length of list is less than 100, so we are fetching the required remaining  messages using roomid , lastMessage Date and the length of  the list:
            if (remainingMessagesList.size() < 100) {
                List<GroupMessageInfoPojo> olderRemainingMessages = groupMessageRepo.fetchingForNextDatePartially(groupId, lastMessageDate, (100 - remainingMessagesList.size()));
                Collections.reverse(remainingMessagesList);
                Collections.reverse(olderRemainingMessages);

                olderRemainingMessages.addAll(remainingMessagesList);
                for (GroupMessageInfoPojo currentMessageObject : remainingMessagesList) {
                    groupMessageDtoList.add(getGroupMessageResponseDto(currentMessageObject,userProfileService.getProfileImage(currentMessageObject.getSenderUserID()),userIdUtilMethods.getUserMobileNo(currentMessageObject.getSenderUserID())));
                }
                return ResponseEntity.ok(new ApiWrapperClass<>(groupMessageDtoList,true,"Remaining Group fetched successfully:"));
            }
            // finally we are returning  the next batch of messages of same date.That means the messages of particular date is  again more than or  equal to 100:,
            Collections.reverse(remainingMessagesList);
            for (GroupMessageInfoPojo currentMessageObject : remainingMessagesList) {
                groupMessageDtoList.add(getGroupMessageResponseDto(currentMessageObject,userProfileService.getProfileImage(currentMessageObject.getSenderUserID()),userIdUtilMethods.getUserMobileNo(currentMessageObject.getSenderUserID())));
            }
            return ResponseEntity.ok(new ApiWrapperClass<>(groupMessageDtoList,true,"Remaining Group fetched successfully:"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

    public ResponseEntity<ApiWrapperClass<Void>> deleteGroupMessage(GroupMessageDto deletingGroupMessageObject, boolean flag, SimpMessagingTemplate messagingTemplate) {
        try {


            if (flag) {
                if (!deletingGroupMessageObject.getSenderID().equals(userIdUtilMethods.extractUserIdFromJwtToken()))
                    throw new RuntimeException("Can't delete a message Globally for a Non-Sender UserID");

                if (deletingGroupMessageObject.isMultipartRequestFlag()) {
                    GroupMessageInfoPojo deletingMessageObject = groupMessageRepo.findByMessageID(deletingGroupMessageObject.getMessageID());
                    if (deletingMessageObject == null) throw new RuntimeException("Message not found");
                    if (cloudinaryService.deleteMultipartFile(deletingMessageObject.getFilePublicID(), deletingMessageObject.getFileType())) {
                        groupMessageRepo.delete(deletingMessageObject);
                    } else throw new RuntimeException("Something went wrong with Cloudinary method");
                }
                else {
                    if (groupMessageRepo.deletingMessage(deletingGroupMessageObject.getMessageID()) == 0) {
                        throw new RuntimeException("Something Went wrong:");
                    }
                }

                messagingTemplate.convertAndSend("/topic/subscribingGroupChatOnline/" + deletingGroupMessageObject.getGroupID(), new ApiWrapperClass<>(deletingGroupMessageObject,true,"message Deleted:"));
            }
            else{
                if(deletingGroupMessageObject.getSenderID().equals(userIdUtilMethods.extractUserIdFromJwtToken())){
                    //Deleting for sender side
                    GroupMessageInfoPojo tempDeletingObject=groupMessageRepo.findByMessageID(deletingGroupMessageObject.getMessageID());
                    if(tempDeletingObject==null) throw  new RuntimeException("Message Not found");
                    tempDeletingObject.setDeletedForSender(true);
                    groupMessageRepo.save(tempDeletingObject);

                }
                else{
                    //deleting message for receiver
                    DeletedMessageRecordForReceiverSideForGroupChat deletingForReceiver=new DeletedMessageRecordForReceiverSideForGroupChat();
                    deletingForReceiver.setDeletedMessageId(deletingGroupMessageObject.getMessageID());
                    deletingForReceiver.setDeletedForReceiverUserId(userIdUtilMethods.extractUserIdFromJwtToken());
                    deletingMessageForReceiverRecorder.save(deletingForReceiver);
                }
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,e.getLocalizedMessage()));
        }

        return  ResponseEntity.ok(new ApiWrapperClass<>(null,true,"message Deleted"));
    }
}
