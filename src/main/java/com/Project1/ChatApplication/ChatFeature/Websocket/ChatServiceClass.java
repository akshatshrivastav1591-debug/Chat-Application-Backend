package com.Project1.ChatApplication.ChatFeature.Websocket;


import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserContacts.UserContactsService;
import com.Project1.ChatApplication.UserProfile.UserProfileService;
import com.Project1.ChatApplication.WrapperClasses.ApiWrapperClass;
import com.cloudinary.api.exceptions.BadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ChatServiceClass {
    @Autowired
    UserChatDetailsInfoRepo chatInfoRepo;
    @Autowired
    USerIdUtilMethods userIdUtilMethods;
    @Autowired
    UserContactsService userContactsService;
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public ResponseEntity<?> isFirstChat(Map<String, Object> ChatInfo) {
try {
        String userID1 = userIdUtilMethods.extractUserIdFromJwtToken();
        Map<String, Object> userID2 = userIdUtilMethods.getUserID((String) ChatInfo.get("ContactNo"));

        UserChatsDetailsPojo chatdetails = chatInfoRepo.findByFirstUserIdAndSecondUserId(userID1, userID2.get("UserId").toString());

        if (chatdetails == null) {
            UserChatsDetailsPojo userChatsDetailsPojo = new UserChatsDetailsPojo();
            userChatsDetailsPojo.setRoomId(Long.parseLong(ChatInfo.get("roomId").toString()));
            userChatsDetailsPojo.setUserId1(userID1);
            userChatsDetailsPojo.setUserId2(userID2.get("UserId").toString());
            userChatsDetailsPojo.setLastMessage("No messages has send till now:");
            userChatsDetailsPojo.setUser1UnseenMessageCount(0L);
            userChatsDetailsPojo.setUser2UnseenMessageCount(0L);
            userChatsDetailsPojo.setSendingTime(LocalTime.now());
            userChatsDetailsPojo.setSendingDate(LocalDate.now());
            userChatsDetailsPojo.setBlocked(false);
            userChatsDetailsPojo.setBlockedBY(null);
            chatInfoRepo.save(userChatsDetailsPojo);
            return ResponseEntity.ok(Map.of("isFirstChat", true, "lastMessage", "No Messages are available:"));
        } else {
            if (chatdetails.isBlocked())
                return ResponseEntity.ok(Map.of("isFirstChat", false, "lastMessage", chatdetails.getLastMessage(), "isBlocked", true, "blockedByUserID", chatdetails.getBlockedBY()));
            else
                return ResponseEntity.ok(Map.of("isFirstChat", false, "lastMessage", chatdetails.getLastMessage(), "isBlocked", false));

        }
    }catch (Exception e){

    return null;
    }
    }

    public ResponseEntity<?> getAllChatsDetails() {
        try {


            List<UserChatsDetailsPojo> chatDetailsList = chatInfoRepo.findAllByFirstUserIdAndSecondUserId(userIdUtilMethods.extractUserIdFromJwtToken());

            List<ChatInfoDto> chatDetailsDto = new ArrayList<>();

            for (UserChatsDetailsPojo tempChatDetails : chatDetailsList) {
                if(!tempChatDetails.isBlocked()||tempChatDetails.getBlockedBY().equals(userIdUtilMethods.extractUserIdFromJwtToken())) {
                    ChatInfoDto chatInfoDto = new ChatInfoDto();
                    if(tempChatDetails.isBlocked()){
                        chatInfoDto.setBlocked(true);
                        chatInfoDto.setBlockedByUserID(tempChatDetails.getBlockedBY());
                    }

                    chatInfoDto.setRoomId(tempChatDetails.getRoomId());
                    chatInfoDto.setLastMessage(tempChatDetails.getLastMessage());

                    if (tempChatDetails.getUserId1().equals(userIdUtilMethods.extractUserIdFromJwtToken())) {
                        chatInfoDto.setSavedName(userContactsService.getSavedName(tempChatDetails.getUserId1(), tempChatDetails.getUserId2()));
                        chatInfoDto.setProfilePicture(userProfileService.getProfileImage(tempChatDetails.getUserId2()));
                        chatInfoDto.setUserId(tempChatDetails.getUserId2());

                        chatInfoDto.setUnseenMessagesCount(tempChatDetails.getUser1UnseenMessageCount());
                    } else {
                        chatInfoDto.setSavedName(userContactsService.getSavedName(tempChatDetails.getUserId2(), tempChatDetails.getUserId1()));
                        chatInfoDto.setProfilePicture(userProfileService.getProfileImage(tempChatDetails.getUserId1()));
                        chatInfoDto.setUserId(tempChatDetails.getUserId1());
                        chatInfoDto.setUnseenMessagesCount(tempChatDetails.getUser2UnseenMessageCount());

                    }
                    chatDetailsDto.add(chatInfoDto);
                }
            }
            return ResponseEntity.ok(Map.of("chatDetailsList", chatDetailsDto));
        } catch (Exception e) {

            return ResponseEntity.status(500).body(Map.of("message", "Something went wrong with server:"));
        }
    }

    public boolean savedLastMessage(Long roomID, String lastMessage, boolean isMultipartRequest, String fileType, LocalDate sendingDate, LocalTime sendingTime, boolean isOtherUserAvailable, String senderUSerId, int arraySize) {
        try {
            UserChatsDetailsPojo userChatsDetailsPojo = chatInfoRepo.findByRoomId(roomID);
            userChatsDetailsPojo.setSendingDate(sendingDate);
            userChatsDetailsPojo.setSendingTime(sendingTime);
            if (!isOtherUserAvailable) {
                if (isMultipartRequest) {
                    if (userChatsDetailsPojo.getUserId1().equals(senderUSerId)) {
                        userChatsDetailsPojo.setUser2UnseenMessageCount(userChatsDetailsPojo.getUser2UnseenMessageCount() + arraySize);
                    } else {
                        userChatsDetailsPojo.setUser1UnseenMessageCount(userChatsDetailsPojo.getUser1UnseenMessageCount() + arraySize);
                    }
                } else {
                    if (userChatsDetailsPojo.getUserId1().equals(senderUSerId)) {
                        userChatsDetailsPojo.setUser2UnseenMessageCount(userChatsDetailsPojo.getUser2UnseenMessageCount() + 1);
                    } else {
                        userChatsDetailsPojo.setUser1UnseenMessageCount(userChatsDetailsPojo.getUser1UnseenMessageCount() + 1);
                    }
                }
            }
            if (isMultipartRequest) userChatsDetailsPojo.setLastMessage(fileType);
            else userChatsDetailsPojo.setLastMessage(lastMessage);
            chatInfoRepo.save(userChatsDetailsPojo);
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public Long unseenMessageCount(Long roomId, String senderUserId) {
        UserChatsDetailsPojo userChatsDetailsPojo = chatInfoRepo.findByRoomId(roomId);
        if (userChatsDetailsPojo.getUserId1().equals(senderUserId))
            return userChatsDetailsPojo.getUser2UnseenMessageCount();
        else return userChatsDetailsPojo.getUser1UnseenMessageCount();
    }

    public Boolean updateUnseenMessageCount(Long roomID, String userId) {
        try {
            UserChatsDetailsPojo userChatsDetailsPojo = chatInfoRepo.findByRoomId(roomID);
            if (userChatsDetailsPojo.getUserId1().equals(userId)) {
                userChatsDetailsPojo.setUser1UnseenMessageCount(0L);
                chatInfoRepo.save(userChatsDetailsPojo);
                return true;
            } else {
                userChatsDetailsPojo.setUser2UnseenMessageCount(0L);
                chatInfoRepo.save(userChatsDetailsPojo);
                return true;
            }

        }catch (Exception e){

            return  false;
        }
    }

    public ResponseEntity<ApiWrapperClass<Long>> blockUser(Long roomID,String receiverId){
        try {
            if (chatInfoRepo.blockingUser(userIdUtilMethods.extractUserIdFromJwtToken(), roomID) == 0) throw new RuntimeException("Something went wrong with sql:");
            messagingTemplate.convertAndSend(
                    "/topic/user/" + receiverId,
                    (Object) (Map.of("isUserBlocked", true))
            );
            return  ResponseEntity.ok(new ApiWrapperClass<>(roomID,true,"user Blocked"));
        }catch (Exception e){

            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong"));
        }
    }
    public ResponseEntity<ApiWrapperClass<Long>> unBlockingUser(Long roomID){
        try {
            UserChatsDetailsPojo unblockingChatDetails=chatInfoRepo.findByRoomId(roomID);
            if(unblockingChatDetails==null) throw new RuntimeException("Chat details is not available");
            if(unblockingChatDetails.getBlockedBY().equals(userIdUtilMethods.extractUserIdFromJwtToken())){
                if(chatInfoRepo.unBlockingUser(roomID)==0) throw  new SQLDataException("Something went wrong with sql");
                else{
                    String receiverID;
                    if(unblockingChatDetails.getBlockedBY().equals(unblockingChatDetails.getUserId1())){
                        receiverID= unblockingChatDetails.getUserId2();
                    }
                    else{
                        receiverID= unblockingChatDetails.getUserId1();
                    }
                    messagingTemplate.convertAndSend(
                            "/topic/user/" + receiverID,
                            (Object) (Map.of("isUserUnBlocked", true))
                    );
                    return ResponseEntity.ok(new ApiWrapperClass<>(null,true,"User unblocked successfully:"));
                }
            }
            else return ResponseEntity.badRequest().body(new ApiWrapperClass<>(roomID,false,"Bad Request:you can't unblock yourself"));

        }catch (Exception e){

            return ResponseEntity.status(500).body(new ApiWrapperClass<>(null,false,"Something went wrong:"));
        }
    }
}