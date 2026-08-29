package com.Project1.ChatApplication.GroupChatFeature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface GroupInfoRepo extends JpaRepository<GroupInfoPojoClass,String> {
GroupInfoPojoClass findByGroupId(String groupId);
    @Modifying
    @Transactional
    @Query("update GroupInfoPojoClass m set m.lastMessage=:newRecentMessage , m.lastMessageSendingTime=:sendingTime, m.lastMessageSendingDate=:sendingDate where m.groupId=:groupID")
    int updatingRecentMessages(String newRecentMessage, String groupID, LocalTime sendingTime, LocalDate sendingDate);



}
