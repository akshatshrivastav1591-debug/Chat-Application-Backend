package com.Project1.ChatApplication.GroupChatFeature.DtoClasses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageContentDto {
    private  String messageContent;
    private LocalDate sendingDate;
    private LocalTime sendingTime;
}
