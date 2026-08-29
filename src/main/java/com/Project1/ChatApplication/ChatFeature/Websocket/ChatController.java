package com.Project1.ChatApplication.ChatFeature.Websocket;


import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupMessageDto;

import com.Project1.ChatApplication.GroupChatFeature.GroupMessageServiceClass;
import com.Project1.ChatApplication.MessageDao.MessageDto;

import com.Project1.ChatApplication.MessageDao.MessageServiceClass;
import com.Project1.ChatApplication.WrapperClasses.ApiWrapperClass;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RestController
@RequiredArgsConstructor

public class ChatController {
    private final Map<Long, ConcurrentHashMap<String, Boolean>> usersOnlineInfo = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RoomUserInfo> subscriptionRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String ,GroupUserInfo> subscriptionRegistryForGroup=new ConcurrentHashMap<>();
    private final Map<String, ConcurrentHashMap<String, Set<String>>> presentUserList = new ConcurrentHashMap<>();


    // simple holder class for single chat feature
    private record RoomUserInfo(Long roomId, String userId) {
    }
    // simple holder class for group chat feature
    private record  GroupUserInfo(String groupID,String userId){}
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    ChatServiceClass chatServiceClass;
    @Autowired
    MessageServiceClass messageServiceClass;
    @Autowired
    GroupMessageServiceClass groupMessageServiceClass;

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public Map<String, Object> DemoMessage(@Payload String message, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable Long roomId) {
        String senderUserID = (String) headerAccessor
                .getSessionAttributes()
                .get("userId");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.readTree(message).get("content").asString();
        boolean isOtherUserActive = mapper.readTree(message).get("isUserAvailable").asBoolean();


        Map<String, Object> isNewContact = messageServiceClass.saveMessage(content, senderUserID, roomId, isOtherUserActive);
        if (!(boolean) isNewContact.get("IsGood")) return null;
        if ((boolean) isNewContact.get("IsGood") && !(boolean) isNewContact.get("IsFirstChatAtReceiverSide")) {

            return Map.of("messageDto", isNewContact.get("messageDto"), "roomId", roomId, "isNewContact", false);
        } else {

            String receiverId = isNewContact.get("receiverUserID").toString();

            messagingTemplate.convertAndSend(
                    "/topic/user/" + receiverId,
                    (Object) (Map.of("isNewContact", true))
            );
            return Map.of("messageDto", isNewContact.get("messageDto"), "roomId", roomId, "isNewContact", false);

        }
    }


    @MessageMapping("/multiPartRequest/{roomId}/{isOtherUserAvailable}")
    @SendTo("/topic/chat/{roomId}")
    public Map<String, Object> multiPartRequest(@Payload String rawArray, @DestinationVariable Long roomId, @DestinationVariable boolean isOtherUserAvailable, SimpMessageHeaderAccessor headerAccessor) {
        String senderUserID = (String) headerAccessor
                .getSessionAttributes()
                .get("userId");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode filesArray = mapper.readTree(rawArray);

        List<JsonNode> fileList = new ArrayList<>();
        for (JsonNode node : filesArray) {
            fileList.add(node);
        }// parses the array
        Map<String, Object> responseList = messageServiceClass.saveMultipartFiles(fileList, roomId, senderUserID, isOtherUserAvailable);
        if (!(boolean) responseList.get("IsGood")) return null;
        if ((boolean) responseList.get("IsGood") && !(boolean) responseList.get("IsFirstChatAtReceiverSide")) {

            return Map.of("messageDto", responseList.get("messageDto"), "roomId", roomId, "isNewContact", false);
        } else {
            String receiverId = responseList.get("receiverUserID").toString();
            messagingTemplate.convertAndSend(
                    "/topic/user/" + receiverId,
                    (Object) (Map.of("isNewContact", true))
            );
            return Map.of("messageDto", responseList.get("messageDto"), "roomId", roomId, "isNewContact", false);

        }
    }

    @MessageMapping("/multiPartRequestForGroupMessaging/{groupId}")
    @SendTo("/topic/groupChat/{groupId}")
    public ApiWrapperClass<List<GroupMessageDto>> multiPartRequestForGroupMessaging(@Payload String rawArray, @DestinationVariable String groupId, SimpMessageHeaderAccessor headerAccessor) {
        String senderUserID = (String) headerAccessor
                .getSessionAttributes()
                .get("userId");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode filesArray = mapper.readTree(rawArray);

        List<JsonNode> fileList = new ArrayList<>();
        for (JsonNode node : filesArray) {
            fileList.add(node);
        }

        Set<String> currentlyActiveMembers=presentUserList.getOrDefault(groupId,new ConcurrentHashMap<>()).keySet();
        return groupMessageServiceClass.saveMultipartRequest(fileList,groupId,senderUserID,currentlyActiveMembers);
    }
    @MessageMapping("/groupChat/{groupId}")
    @SendTo("/topic/groupChat/{groupId}")
    public ApiWrapperClass<GroupMessageDto> groupChat(@Payload String  message, @DestinationVariable String groupId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String senderUserID = (String) headerAccessor
                    .getSessionAttributes()
                    .get("userId");


         ObjectMapper mapper = new ObjectMapper();
        String messageBody = mapper.readTree(message).get("content").asString();
        Set<String> currentlyActiveMembers=presentUserList.getOrDefault(groupId,new ConcurrentHashMap<>()).keySet();
        return groupMessageServiceClass.saveMessage(messageBody,groupId,senderUserID,currentlyActiveMembers);

        } catch (JacksonException e) {

            return null;
        }
    }
    @MessageMapping("/deletionOfMessagesForAll/{groupId}")
    @SendTo("/topic/subscribingGroupChatOnline/{groupID}")
    public  ApiWrapperClass<String> publishingTheDeletionMethodInfo(@Payload String messageID,SimpMessageHeaderAccessor headerAccessor){
        String senderUserID = (String) headerAccessor
                .getSessionAttributes()
                .get("userId");
        ObjectMapper mapper = new ObjectMapper();
        String messageId = mapper.readTree(messageID).get("content").asString();
        return new ApiWrapperClass<>(messageId,true,"message id");
    }
    @EventListener
    public void unsubscribingUser(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String subscriptionId = headers.getSubscriptionId(); // ✅ THIS is available, destination is not

        RoomUserInfo info = subscriptionRegistry.remove(subscriptionId); // also cleans up the registry
        if (info != null) { // not one of our isOnline subscriptions
            usersOnlineInfo.computeIfAbsent(info.roomId(), k -> new ConcurrentHashMap<>()).put(info.userId(), false);
        ConcurrentHashMap<String, Boolean> userAvailability = usersOnlineInfo.get(info.roomId());
        Map<String, Object> payload = Map.of("userAvailability", userAvailability);
        messagingTemplate.convertAndSend("/topic/isOnline/" + info.roomId(), Optional.of(payload));
            }
        else{
            GroupUserInfo groupInfo = subscriptionRegistryForGroup.remove(subscriptionId);
            if (groupInfo != null) {
                ConcurrentHashMap<String, Set<String>> usersInGroup = presentUserList.get(groupInfo.groupID());
                if (usersInGroup != null) {
                    Set<String> userSubscriptions = usersInGroup.get(groupInfo.userId());
                    if (userSubscriptions != null) {
                        userSubscriptions.remove(subscriptionId);
                        if (userSubscriptions.isEmpty()) {
                            usersInGroup.remove(groupInfo.userId());
                        }
                    }
                }
            }
        }
    }

    @EventListener
    public void disconnectingUser(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        Set<String> subscriptionIds = sessionSubscriptions.remove(sessionId);
        if (subscriptionIds == null || subscriptionIds.isEmpty()) return;

        for (String subscriptionId : subscriptionIds) {
            RoomUserInfo info = subscriptionRegistry.remove(subscriptionId);
            if (info != null) {
                usersOnlineInfo.computeIfAbsent(info.roomId(), k -> new ConcurrentHashMap<>()).put(info.userId(), false);
                ConcurrentHashMap<String, Boolean> userAvailability = usersOnlineInfo.get(info.roomId());
                Map<String, Object> payload = Map.of("userAvailability", userAvailability);
                messagingTemplate.convertAndSend("/topic/isOnline/" + info.roomId(), Optional.of(payload));
                continue; // it was single-chat, move to next subscriptionId
            }

            // wasn't single-chat — check if it was a group subscription
            GroupUserInfo groupInfo = subscriptionRegistryForGroup.remove(subscriptionId);
            if (groupInfo != null) {
                ConcurrentHashMap<String, Set<String>> usersInGroup = presentUserList.get(groupInfo.groupID());
                if (usersInGroup != null) {
                    Set<String> userSubscriptions = usersInGroup.get(groupInfo.userId());
                    if (userSubscriptions != null) {
                        userSubscriptions.remove(subscriptionId);
                        if (userSubscriptions.isEmpty()) {
                            usersInGroup.remove(groupInfo.userId());
                        }
                    }
                }
            }
        }
    }


    @EventListener
    public void subscribingUser(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();
        String sessionId = headers.getSessionId();
        if (destination == null || (!destination.startsWith("/topic/isOnline")&& !destination.startsWith("/topic/subscribingGroupChatOnline"))) return;


        if(destination.startsWith("/topic/isOnline")){
            String userId = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("userId");
            String roomIdStr = destination.substring(destination.lastIndexOf("/") + 1);
            Long roomId = Long.parseLong(roomIdStr);
            String subscriptionId = headers.getSubscriptionId(); // ✅ available on subscribe

            // remember this subscription so we can recall it on unsubscribe
            subscriptionRegistry.put(subscriptionId, new RoomUserInfo(roomId, userId));
            sessionSubscriptions.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(subscriptionId);
            usersOnlineInfo.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, true);
            ConcurrentHashMap<String, Boolean> userAvailability = usersOnlineInfo.get(roomId);
            Map<String, Object> payload = Map.of("userAvailability", userAvailability);
            messagingTemplate.convertAndSend("/topic/isOnline/" + roomId, Optional.of(payload));
        }
        if(destination.startsWith("/topic/subscribingGroupChatOnline")){
            String userId = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("userId");
            String groupID = destination.substring(destination.lastIndexOf("/") + 1);
            String subscriptionId= headers.getSubscriptionId();
            assert subscriptionId != null;
            subscriptionRegistryForGroup.put(subscriptionId, new GroupUserInfo(groupID, userId));
            sessionSubscriptions.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(subscriptionId);
            presentUserList
                    .computeIfAbsent(groupID, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                    .add(subscriptionId);
         }
    }


    @PostMapping("/isNewContact")
    public ResponseEntity<?> viaContacts(@RequestBody Map<String, Object> chatDetails) {

        return chatServiceClass.isFirstChat(chatDetails);
    }

    @GetMapping("/getAllChatDetails")
    public ResponseEntity<?> getAllChatDetails() {
        return chatServiceClass.getAllChatsDetails();
    }

    @GetMapping("/getAllMessages/{roomId}")
    public ResponseEntity<List<MessageDto>> getAllMessages(@PathVariable Long roomId) {
        return messageServiceClass.getMessages(roomId);

    }

    @DeleteMapping("/deleteMesssage/{flag}")
    public ResponseEntity<Object> deleteMessage(@RequestBody MessageDto deletingMessageObject, @PathVariable boolean flag) {

        return messageServiceClass.deleteMessage(deletingMessageObject, flag, messagingTemplate);

    }

    @GetMapping("/fetchRemainingMessage/{roomId}/{lastMessageDate}/{lastMessageTime}")
    public ResponseEntity<List<MessageDto>> getRemainingMessages(@PathVariable Long roomId, @PathVariable LocalTime lastMessageTime, @PathVariable LocalDate lastMessageDate) {
        return messageServiceClass.getRemainingMessages(roomId, lastMessageDate, lastMessageTime);
    }
   @GetMapping("/getGroupMessages/{groupID}")
    public ResponseEntity<ApiWrapperClass<List<GroupMessageDto>>> getGroupMessages(@PathVariable String groupID){
        return groupMessageServiceClass.getGroupMessages(groupID);
   }
    @GetMapping("/getRemainingGroupMessage/{groupId}/{lastMessageDate}/{lastMessageTime}")
    public ResponseEntity<ApiWrapperClass<List<GroupMessageDto>>> getRemainingGroupMessages(@PathVariable String groupId, @PathVariable LocalTime lastMessageTime, @PathVariable LocalDate lastMessageDate) {
        return groupMessageServiceClass.getRemainingGroupMessage(groupId,lastMessageDate,lastMessageTime);
    }
@DeleteMapping("/deleteGroupMessage/{flag}")
    public ResponseEntity<ApiWrapperClass<Void>> deleteGroupMessage(@RequestBody GroupMessageDto deletingGroupMessageObject,@PathVariable boolean flag){
       return groupMessageServiceClass.deleteGroupMessage(deletingGroupMessageObject,flag,messagingTemplate);
    }
@PutMapping("/BlockUser")
    public ResponseEntity<ApiWrapperClass<Long>> blockUser(@RequestBody ChatInfoDto blockedUserObject){
        return chatServiceClass.blockUser(blockedUserObject.getRoomId(),blockedUserObject.getUserId());
}
@PutMapping("/UnBlockUser/{roomID}")
    public ResponseEntity<ApiWrapperClass<Long>> unBlockUser(@PathVariable Long roomID){
        return chatServiceClass.unBlockingUser(roomID);
    }
}

