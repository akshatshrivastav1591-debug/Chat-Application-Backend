package com.Project1.ChatApplication.MessageDao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Getter
@Setter
public class MessageDto {
    private String messageID; //1
    private String messageContent; //2
    private LocalTime sendingTime; //3
    private LocalDate sendingDate; //4
    private String senderID; //5
    private boolean isCheckedByReceiver; //6
    private boolean isSend; //7
    private boolean isMultipartRequest; //8
    private String fileUrl; //9
    private String fileType; //10
    private String fileName; //11
    private  String fileSize; //12
    private Long unseenMessageCount;

}
