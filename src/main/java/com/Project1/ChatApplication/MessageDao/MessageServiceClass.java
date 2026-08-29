package com.Project1.ChatApplication.MessageDao;


import com.Project1.ChatApplication.ChatFeature.Websocket.ChatServiceClass;
import com.Project1.ChatApplication.RoomID.RoomIdService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.UserContacts.UserContactsService;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Service
public class MessageServiceClass {
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private RoomIdService roomIdService;
    @Autowired
    private ChatServiceClass chatServiceClass;
    @Autowired
    private UserContactsService userContactsService;
    @Autowired
    private USerIdUtilMethods uSerIdUtilMethods;
    @Autowired
    private  CloudinaryServiceImplForMulitPartRequest cloudinaryServiceImplForMulitPartRequest;

    public Map<String, Object> saveMessage(String messageContent, String senderUserID, Long roomId,boolean isOtherUserActive) {

        try {

            MessagePojo messagePojo = new MessagePojo();
            messagePojo.setRoomId(roomId);
            messagePojo.setSenderUserID(senderUserID);
            messagePojo.setMessageContent(messageContent);
            messagePojo.setSendingTime(LocalTime.now());
            messagePojo.setSendingDate(LocalDate.now());
            messagePojo.setCheckedByReceiver(isOtherUserActive);
            messagePojo.setMultipartRequest(false);
            messagePojo.setSend(true);
            messageRepo.save(messagePojo);
            if (chatServiceClass.savedLastMessage(roomId, messageContent, false, null,messagePojo.getSendingDate(),messagePojo.getSendingTime(),isOtherUserActive,senderUserID,0)) {
                Map<String, Object> response = (userContactsService.savingNewContactOnReceiverSide(roomIdService.getRecieverID(roomId, senderUserID), senderUserID));
                if (!(boolean) response.get("IsGood")) return Map.of("IsGood", false);
                MessageDto messageDto = new MessageDto();
                messageDto.setMessageID(messagePojo.getMessageID());
                messageDto.setSenderID(messagePojo.getSenderUserID());
                messageDto.setMessageContent(messagePojo.getMessageContent());
                messageDto.setSendingTime(messagePojo.getSendingTime());
                messageDto.setSendingDate(messagePojo.getSendingDate());
                messageDto.setCheckedByReceiver(messagePojo.isCheckedByReceiver());
                messageDto.setUnseenMessageCount(chatServiceClass.unseenMessageCount(roomId,senderUserID));
                if (!(boolean) response.get("IsFirstChat")) {

                    return Map.of("IsGood", true, "IsFirstChatAtReceiverSide", false, "messageDto", messageDto);
                } else {

                    return Map.of("IsGood", true, "IsFirstChatAtReceiverSide", true, "receiverUserID", roomIdService.getRecieverID(roomId, senderUserID),"messageDto", messageDto);
                }
            }
            return Map.of("IsGood", false);
        } catch (Exception e) {
            return null;
        }

    }

    public ResponseEntity<List<MessageDto>> getMessages(Long roomID) {
        //fetching the first batch of Messages

        try {
            List<MessagePojo> messagePojoList = messageRepo.findByRoomId(roomID);
            Collections.reverse(messagePojoList);
            List<MessageDto> messageDtoList = new ArrayList<>();
            if (messagePojoList.isEmpty()){

                return ResponseEntity.status(204).body(Collections.emptyList());}
            for (MessagePojo messageList : messagePojoList) {
                if(messageList.getSenderUserID().equals(uSerIdUtilMethods.extractUserIdFromJwtToken())&& !messageList.isDeletedForSender()){
                    messageDtoList.add(getMessageDto(messageList));
                }
                if(!messageList.getSenderUserID().equals(uSerIdUtilMethods.extractUserIdFromJwtToken())&&!messageList.isDeletedForReceiever()){
                    messageDtoList.add(getMessageDto(messageList));
                }

            }
            messageRepo.updateCheckedByUser(roomID, uSerIdUtilMethods.extractUserIdFromJwtToken());
           if(chatServiceClass.updateUnseenMessageCount(roomID, uSerIdUtilMethods.extractUserIdFromJwtToken())){
            return ResponseEntity.ok(messageDtoList);
           }
           else{
                throw new RuntimeException();
           }
        } catch (Exception e) {

            return ResponseEntity.status(500).body(null);
        }


    }

    public Map<String, Object> saveMultipartFiles(List<JsonNode> fileArray, Long roomId,String senderUserId,boolean isOtherUserAvailable) {

        List <MessagePojo> messagePojoList=new ArrayList<>();
        try {
            if (fileArray.isEmpty())
                return null;
//            String senderUserId = uSerIdUtilMethods.extractUserIdFromJwtToken();
            for (JsonNode file : fileArray) {
                MessagePojo messagePojo = new MessagePojo();
                messagePojo.setRoomId(roomId);
                messagePojo.setSenderUserID(senderUserId);
                messagePojo.setMultipartRequest(true);
                messagePojo.setSendingDate(LocalDate.now());
                messagePojo.setSendingTime(LocalTime.now());
                messagePojo.setCheckedByReceiver(isOtherUserAvailable);
                messagePojo.setMessageContent(file.get("attachedMessage").asString());
                messagePojo.setFilePublicID(file.get("filePublicId").asString());
                messagePojo.setFileUrl(file.get("fileUrl").asString());
                messagePojo.setFileType(file.get("fileType").asString());
                messagePojo.setFileName(file.get("fileName").asString());
                messagePojo.setFileSize(file.get("fileSize").asString());
                messagePojo.setSend(true);
                messageRepo.save(messagePojo);
                messagePojoList.add(messagePojo);
            }
            if (chatServiceClass.savedLastMessage(roomId, null, true, fileArray.getLast().get("fileType").asString(),messagePojoList.getLast().getSendingDate(), messagePojoList.getLast().getSendingTime(),isOtherUserAvailable,senderUserId, fileArray.size()))
         {
                Map<String, Object> savedAtReceiverSideResponse = userContactsService.savingNewContactOnReceiverSide(roomIdService.getRecieverID(roomId, senderUserId), senderUserId);
                if (!(boolean) savedAtReceiverSideResponse.get("IsGood")) return Map.of("IsGood", false);
             List<MessageDto> response = new ArrayList<>();
             for (MessagePojo fetchedMessage : messagePojoList) {
                 response.add(getMessageDto(fetchedMessage));
             }
             response.get(response.size() - 1)
                     .setUnseenMessageCount(chatServiceClass.unseenMessageCount(roomId, senderUserId));
                if (!(boolean) savedAtReceiverSideResponse.get("IsFirstChat")) {
                    return Map.of("IsGood", true, "IsFirstChatAtReceiverSide", false, "messageDto", response);
                } else {
                    return Map.of("IsGood", true, "IsFirstChatAtReceiverSide", true, "receiverUserID", roomIdService.getRecieverID(roomId, senderUserId),"messageDto", response);

                }
            }
            return Map.of("IsGood", false);
        } catch (Exception e) {

            return null;
        }

    }




    private static @NonNull MessageDto getMessageDto(MessagePojo fetchedMessage) {
        MessageDto sendingMessage=new MessageDto();
        sendingMessage.setMessageID(fetchedMessage.getMessageID()); //1
        sendingMessage.setMessageContent(fetchedMessage.getMessageContent());//2
        sendingMessage.setFileUrl(fetchedMessage.getFileUrl()); //3
        sendingMessage.setSendingTime(fetchedMessage.getSendingTime()); //4
        sendingMessage.setSendingDate(fetchedMessage.getSendingDate()); //5
        sendingMessage.setCheckedByReceiver(fetchedMessage.isCheckedByReceiver()); //6
        sendingMessage.setSenderID(fetchedMessage.getSenderUserID()); //7
        sendingMessage.setSend(fetchedMessage.isSend());//8
        sendingMessage.setMultipartRequest(fetchedMessage.isMultipartRequest()); //9
        sendingMessage.setFileType(fetchedMessage.getFileType()); //10
        sendingMessage.setFileName(fetchedMessage.getFileName()); //11
        sendingMessage.setFileSize(fetchedMessage.getFileSize()); //12

        return sendingMessage;
    }

    public ResponseEntity<List<MessageDto>> getRemainingMessages(Long roomId, LocalDate lastMessageDate, LocalTime lastMessageTime) {
        //fetching the Remaining  messages:
        try {
            //fetching the remaining messages of last message date:
            List<MessagePojo> remainingMessagesList = messageRepo.findByRoomIdAndSendingDateAndSendingTime(roomId, lastMessageDate, lastMessageTime);
            List<MessageDto> response = new ArrayList<>();
            if (remainingMessagesList.isEmpty()) {
                //if the previous method returning null, we are fetching the next batch of messages from next date:
                List<MessagePojo> nextDateMessage = messageRepo.fetchingForNextDate(roomId, lastMessageDate);
                if (nextDateMessage.isEmpty()) return ResponseEntity.status(204).body(null);
                else {
                    Collections.reverse(nextDateMessage);


                    for (MessagePojo message : nextDateMessage) {
                        response.add(getMessageDto(message));
                    }
                    return ResponseEntity.ok(response);
                }
            }
            //if the messageRepo.findByRoomIdAndSendingDateAndSendingTime(roomId, lastMessageDate, lastMessageTime); method is returning not null List ,but the
            //length of list is less than 100, so we are fetching the required remaining  messages using roomid , lastMessage Date and the length of  the list:
            if (remainingMessagesList.size() < 100) {
                List<MessagePojo> olderRemainingMessages = messageRepo.fetchingForNextDatePartially(roomId, lastMessageDate, (100 - remainingMessagesList.size()));
                Collections.reverse(remainingMessagesList);
                Collections.reverse(olderRemainingMessages);

                    olderRemainingMessages.addAll(remainingMessagesList);
                for (MessagePojo message : remainingMessagesList) {
                    response.add(getMessageDto(message));
                }
                return ResponseEntity.ok(response);
            }
            // finally we are returning  the next batch of messages of same date.That means the messages of particular date is  again more than or  equal to 100:,
            Collections.reverse(remainingMessagesList);
            for (MessagePojo message : remainingMessagesList) {
                response.add(getMessageDto(message));
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){

            return ResponseEntity.status(500).body(null);
        }
    }
//Utility Method for chatService Class
public int countingUncheckedMethod(Long roomId,String currentUserID){
        return messageRepo.countingUncheckedMessages(roomId,currentUserID);
}
//For Deleting Message:
public ResponseEntity<Object> deleteMessage(MessageDto messageDto, boolean flag, SimpMessagingTemplate messagingTemplate) {
        try {
            //Deleting message for sender side like wahtsApp's delete for me (Sended Message);
            if(messageDto.getSenderID().equals(uSerIdUtilMethods.extractUserIdFromJwtToken())&& !flag){
                MessagePojo deletingMessage=messageRepo.findByMessageID(messageDto.getMessageID());
                if(deletingMessage.isDeletedForReceiever()){
                    if(!deletingMessage.isMultipartRequest()) {
                        messageRepo.delete(deletingMessage);
                        return ResponseEntity.ok(Map.of("Response", "messageDeleted"));
                    }
                    else {
                        boolean response=cloudinaryServiceImplForMulitPartRequest.deleteMultipartFile(deletingMessage.getFilePublicID(), deletingMessage.getFileType());
                        if(response){
                            messageRepo.delete(deletingMessage);
                            return ResponseEntity.ok(Map.of("Response", "messageDeleted"));
                        }
                        else return  ResponseEntity.status(500).body(Map.of("Response","Something Went wrong with server"));
                    }
                }
                else {
                    deletingMessage.setDeletedForSender(true);
                    messageRepo.save(deletingMessage);
                    return  ResponseEntity.ok(Map.of("Response","Messages Deleted"));
                }
            }
            //Deleting message for sender side like wahtsapp's delete for me (RecivedMessage Message);
            if(!messageDto.getSenderID().equals(uSerIdUtilMethods.extractUserIdFromJwtToken())&& !flag){
                MessagePojo deletingMessage=messageRepo.findByMessageID(messageDto.getMessageID());
                if(deletingMessage.isDeletedForSender()){
                    if(!deletingMessage.isMultipartRequest()) {
                        messageRepo.delete(deletingMessage);
                        return ResponseEntity.ok(Map.of("Response", "messageDeleted"));
                    }
                    else{
                        boolean response=cloudinaryServiceImplForMulitPartRequest.deleteMultipartFile(deletingMessage.getFilePublicID(), deletingMessage.getFileType());
                        if(response){
                            messageRepo.delete(deletingMessage);
                            return ResponseEntity.ok(Map.of("Response", "messageDeleted"));
                        }
                        else return  ResponseEntity.status(500).body(Map.of("Response","Something Went wrong with server"));
                    }
                }
                else{
                    deletingMessage.setDeletedForReceiever(true);
                    messageRepo.save(deletingMessage);
                    return ResponseEntity.ok(Map.of("Response","messageDeleted"));
                }
            }
                  // For Deleting Message for everyone (Flag===true means this)
                  MessagePojo deletingMessage=messageRepo.findByMessageID(messageDto.getMessageID());
                   if(!deletingMessage.isMultipartRequest()){
                   messageRepo.delete(deletingMessage);
                   String message= deletingMessage.getMessageID();
                   Long roomId= deletingMessage.getRoomId();
                   Map<String,Object> payload=Map.of("messageDeletionID",message);
            messagingTemplate.convertAndSend("/topic/isOnline/" + roomId, Optional.of(payload));
                   return ResponseEntity.ok(Map.of("Response","Message Deleted"));
                   }
                   else{
                       boolean response=cloudinaryServiceImplForMulitPartRequest.deleteMultipartFile(deletingMessage.getFilePublicID(), deletingMessage.getFileType());
                       if(response){
                           messageRepo.delete(deletingMessage);
                           String message= deletingMessage.getMessageID();
                           Long roomId= deletingMessage.getRoomId();
                           Map<String,Object> payload=Map.of("messageDeletionID",message);
                           messagingTemplate.convertAndSend("/topic/isOnline/" + roomId, Optional.of(payload));
                           return ResponseEntity.ok(Map.of("Response","Message Deleted"));
                       }
                       return  ResponseEntity.status(500).body(Map.of("Response","Something Went wrong with server"));
                   }

        }catch (Exception e){
            return  ResponseEntity.status(500).body(Map.of("Response","Something went wrong with server"));
        }

    }
}