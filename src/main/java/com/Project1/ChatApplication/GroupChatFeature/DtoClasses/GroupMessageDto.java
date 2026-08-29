package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageDto {
    private String groupID;
    private String messageID;
    private String senderID;
    private String senderProfilePicture;
    private String senderName;
    private String messageContent;
    private LocalTime sendingTime;
    private LocalDate sendingDate;
    private boolean multipartRequestFlag;
    private String fileUrl;
    private String fileType;
    private String fileName;
    private  String fileSize;
}
