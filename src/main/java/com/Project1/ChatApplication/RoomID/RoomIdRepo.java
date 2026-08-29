package com.Project1.ChatApplication.RoomID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface RoomIdRepo extends JpaRepository<RoomIdPojo,Long> {
    @Query("""
    SELECT r FROM RoomIdPojo r
    WHERE 
        (r.firstUserId = :FirstUserID AND r.secondUserId = :SecondUserID)
        OR
        (r.firstUserId = :SecondUserID AND r.secondUserId = :FirstUserID)
""")
    RoomIdPojo findByFirstUserIdAndSecondUserId(String FirstUserID,String SecondUserID);
   RoomIdPojo findByRoomId(Long roomId);
}