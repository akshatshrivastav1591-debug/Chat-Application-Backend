package com.Project1.ChatApplication.MessageDao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MessagePojo {
    @Id
    private String messageID= UUID.randomUUID().toString();
    private Long roomId;
    private  String senderUserID;
    private String messageContent;
    private LocalTime sendingTime;
    private LocalDate sendingDate;
    private boolean  isCheckedByReceiver;
    @Column(name = "ismultipartrequest")
    private boolean isMultipartRequest;
    private String fileUrl;
    @Column(name = "issend")
    private  boolean isSend;
    @Column(name="filepublicid")
    private String filePublicID;
    @Column(name="filetype")
    private  String fileType;
    @Column(name="filename")
    private  String fileName;
    @Column(name = "filesize")
    private  String fileSize;
    @Column(name = "deletedforreciever")
    private boolean deletedForReceiever;
    @Column(name = "deletedforsender")
    private boolean deletedForSender;

}
