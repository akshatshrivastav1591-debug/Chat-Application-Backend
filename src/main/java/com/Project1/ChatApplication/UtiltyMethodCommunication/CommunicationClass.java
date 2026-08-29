package com.Project1.ChatApplication.UtiltyMethodCommunication;

import com.Project1.ChatApplication.MessageDao.MessageServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunicationClass {
    @Autowired
    MessageServiceClass messageServiceClass;
    public int  uncheckedMessageCounter(Long roomId,String currentUserId){
        return messageServiceClass.countingUncheckedMethod(roomId,currentUserId);
    }
}
