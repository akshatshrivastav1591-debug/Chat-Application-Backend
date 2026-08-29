package com.Project1.ChatApplication.GroupChatFeature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeletedMessageRecordRepo extends JpaRepository<DeletedMessageRecordForReceiverSideForGroupChat,Long> {
    @Query("select EXISTS (select 1 from DeletedMessageRecordForReceiverSideForGroupChat m where m.deletedMessageId=:messageID and m.deletedForReceiverUserId=:UserId)")
    boolean isThisMessageDeletedForThisReceiver(String messageID,String UserId);
}
