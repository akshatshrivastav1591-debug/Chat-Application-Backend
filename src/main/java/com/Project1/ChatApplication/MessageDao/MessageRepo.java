package com.Project1.ChatApplication.MessageDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MessageRepo extends JpaRepository<MessagePojo,Long> {
    //for first fetching of messages
    @Query("SELECT m FROM MessagePojo m WHERE m.roomId = :roomID  ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<MessagePojo> findByRoomId(Long roomID);

   //for secound fetching  of messages
    @Query("SELECT m FROM MessagePojo m WHERE m.roomId = :roomID and m.sendingDate=:sendingDate and m.sendingTime<:sendingTime ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<MessagePojo> findByRoomIdAndSendingDateAndSendingTime(Long roomID, LocalDate sendingDate, LocalTime sendingTime);
    //for fetching the next batch of messages if messages of any particular date is ==0
    @Query("SELECT m FROM MessagePojo m WHERE m.roomId = :roomID and m.sendingDate<:sendingDate ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<MessagePojo> fetchingForNextDate(Long roomID, LocalDate sendingDate);
    //for fetching the next batch of messages if messages of any particular date is <100 but not==0
    @Query("SELECT m FROM MessagePojo m WHERE m.roomId = :roomID and m.sendingDate<:sendingDate ORDER BY m.sendingDate DESC,m.sendingTime DESC limit :requiredNumbers")
    List<MessagePojo> fetchingForNextDatePartially(Long roomID, LocalDate sendingDate,int requiredNumbers);
    @Query("Select count(m.messageID) FROM MessagePojo m where m.roomId = :roomID and m.senderUserID!=:currentUserID and m.isCheckedByReceiver=false")
    int countingUncheckedMessages(Long roomID,String currentUserID);



    //Message Modification Query:
    @Modifying
    @Transactional
    @Query("update MessagePojo m  set m.isCheckedByReceiver=true where m.roomId = :roomID and m.senderUserID!=:userId")
    int  updateCheckedByUser(@Param("roomID") Long roomID , @Param("userId") String userId);

    MessagePojo findByMessageID(String messageId);
}