package com.Project1.ChatApplication.MessageDao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Getter
@Setter
public class MultiPartRequestDtoPojo {
    private String fileUrl;
    private  String attachedMessages;
    private String messageID;
    private LocalTime sendingTime;
    private LocalDate sendingDate;
}
