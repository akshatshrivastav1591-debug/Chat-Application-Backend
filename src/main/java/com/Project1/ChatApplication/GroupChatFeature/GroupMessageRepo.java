package com.Project1.ChatApplication.GroupChatFeature;

import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupMessageDto;
import com.Project1.ChatApplication.MessageDao.MessagePojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface GroupMessageRepo extends JpaRepository<GroupMessageInfoPojo,String> {

    //for first fetching of messages
    @Query("SELECT m FROM GroupMessageInfoPojo m WHERE m.groupID = :groupID  ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<GroupMessageInfoPojo> findByGroupId(String groupID);
    //for secound fetching  of messages
    @Query("SELECT m FROM GroupMessageInfoPojo m WHERE m.groupID = :groupID and m.sendingDate=:sendingDate and m.sendingTime<:sendingTime ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<GroupMessageInfoPojo> findByGroupIdAndSendingDateAndSendingTime(String groupID, LocalDate sendingDate, LocalTime sendingTime);
    //for fetching the next batch of messages if messages of any particular date is ==0
    @Query("SELECT m FROM GroupMessageInfoPojo m WHERE m.groupID = :groupID and m.sendingDate<:sendingDate ORDER BY m.sendingDate DESC,m.sendingTime DESC limit 100")
    List<GroupMessageInfoPojo> fetchingForNextDate(String groupID, LocalDate sendingDate);
    //for fetching the next batch of messages if messages of any particular date is <100 but not==0
    @Query("SELECT m FROM GroupMessageInfoPojo m WHERE m.groupID = :groupID and m.sendingDate<:sendingDate ORDER BY m.sendingDate DESC,m.sendingTime DESC limit :requiredNumbers")
    List<GroupMessageInfoPojo> fetchingForNextDatePartially(String groupID, LocalDate sendingDate,int requiredNumbers);
     //for fetching the uncheckedMessage  for uncheckedMessageCount
    @Query("select m from GroupMessageInfoPojo m where m.messageID != :lastCheckedMessageID" +
            " and m.sendingDate>= :lastCheckedMessageDate" +
            " and  m.sendingTime>= : lastCheckedMessageTime" +
            " and m.groupID= :groupID")
    List <GroupMessageInfoPojo> fetchingUncheckedMessageList(String lastCheckedMessageID,
                                                             String  groupID,
                                                             LocalDate lastCheckedMessageDate,
                                                             LocalTime lastCheckedMessageTime
                                                             );
    @Modifying
    @Transactional
    @Query("delete from GroupMessageInfoPojo m where m.messageID=:deletingMessageId")
    int deletingMessage(String deletingMessageId);
    @Query("Select m from GroupMessageInfoPojo m  where m.messageID=:deletingMessageId")
    GroupMessageInfoPojo findByMessageID(String deletingMessageId);
}
