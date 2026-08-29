package com.Project1.ChatApplication.GroupChatFeature;

import com.Project1.ChatApplication.UserContacts.UserContactPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface GroupMembersInfoRepo extends JpaRepository<GroupMemberPojo,Long> {
    List<GroupMemberPojo> findByUserId(String userId);
    GroupMemberPojo findByUserIdAndGroupId(String userID,String groupID);
    List<GroupMemberPojo> findByGroupId(String groupId);
    //updating the lastCheckedMessage for some active Users:
    @Modifying
    @Transactional
    @Query("update GroupMemberPojo m set m.lastCheckedMessageID= :messageID where m.groupId=:groupID and m.userId=:userID")
    int updatingLastCheckedMessage(String userID,String groupID,String messageID);
    //updating the lastCheckedMessage for some All Users:

   //updating the unseenMessage counter for normal message
    @Modifying
    @Transactional
    @Query("update GroupMemberPojo m set m.uncheckedMessageCounter=m.uncheckedMessageCounter+1 where " +
            "m.groupId=:groupID " +
            "and (m.lastCheckedMessageID is null or m.lastCheckedMessageID != :messageID) ")

    int updatingUnseenMessageCount(String groupID,String messageID);
    //updating the unseenMessage counter for multipart message
    @Modifying
    @Transactional
    @Query("update GroupMemberPojo m set m.uncheckedMessageCounter=m.uncheckedMessageCounter+:unseenMessageLength where " +
            "m.groupId=:groupID " +
            "and (m.lastCheckedMessageID is null or m.lastCheckedMessageID != :messageID) ")

    int updatingUnseenMessageCountForMultipartMessage(String groupID,String messageID,int unseenMessageLength);

    //updating the UncheckedMessageCounter to 0
    @Modifying
    @Transactional
    @Query("update GroupMemberPojo m set m.uncheckedMessageCounter=0 where " +
            "m.groupId=:groupID " +
            "and m.userId=:userID ")

    int updatingUnseenMessageCountTo0(String groupID,String userID);
@Query("select c from UserContactPojo c \n" +
        "Inner join GroupMemberPojo m on c.savedUserID=m.userId\n" +
        "where c.savedBy=:savedByUserID and m.groupId=:groupID")
    List<UserContactPojo> contactInGroup(String savedByUserID,String groupID);
}
