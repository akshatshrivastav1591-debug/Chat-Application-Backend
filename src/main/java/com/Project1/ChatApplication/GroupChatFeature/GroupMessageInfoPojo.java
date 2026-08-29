package com.Project1.ChatApplication.GroupChatFeature;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageInfoPojo {
    @Id
    private String messageID= UUID.randomUUID().toString();
    private String groupID;
    private  String senderUserID;
    private String messageContent;
    private LocalTime sendingTime;
    private LocalDate sendingDate;
    private boolean isMultipartRequest;
    private String fileUrl;
    private  boolean isSend;
    private String filePublicID;
    private  String fileType;
    private  String fileName;
    private  String fileSize;
    private boolean deletedForSender;
}
