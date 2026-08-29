package com.Project1.ChatApplication.ChatFeature.Websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatInfoDto {
    private String savedName;
    private String lastMessage;
    private String profilePicture;
    private Long roomId;
    private  String userId;
    private Long unseenMessagesCount;
    private  boolean blocked;
    private  String blockedByUserID;

}
