package com.Project1.ChatApplication.ChatFeature.Websocket;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;


public interface UserChatDetailsInfoRepo extends JpaRepository<UserChatsDetailsPojo,Integer> {
    @Query("""
    SELECT r FROM UserChatsDetailsPojo r
    WHERE 
        (r.UserId1 = :FirstUserID AND r.UserId2 = :SecondUserID)
        OR
        (r.UserId1 = :SecondUserID AND r.UserId2 = :FirstUserID)
         
""")
    UserChatsDetailsPojo findByFirstUserIdAndSecondUserId(String FirstUserID, String SecondUserID);
    @Query("""
    SELECT r FROM UserChatsDetailsPojo r
    WHERE 
        (r.UserId1 = :UserId)
        OR
        (r.UserId2 = :UserId)
         ORDER BY r.sendingDate DESC, r.sendingTime DESC limit 50
""")
    List <UserChatsDetailsPojo> findAllByFirstUserIdAndSecondUserId(String UserId);

    UserChatsDetailsPojo findByRoomId(Long roomId);
    @Modifying
    @Transactional
    @Query("update UserChatsDetailsPojo c set c.blocked=true  , c.blockedBY=:userID where c.roomId=:roomID")
    int blockingUser(String userID,Long roomID);

    @Modifying
    @Transactional
    @Query("update UserChatsDetailsPojo c set c.blocked=false  , c.blockedBY= null where c.roomId=:roomID")
    int unBlockingUser(Long roomID);
}
