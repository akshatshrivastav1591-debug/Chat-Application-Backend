package com.Project1.ChatApplication.RoomID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class RoomIdService {
    @Autowired
    private RoomIdRepo roomIdRepo;

    public boolean roomIDGeneration(String userID1, String userID2) {
        try {
            if(roomIdRepo.findByFirstUserIdAndSecondUserId(userID1, userID2) != null) return true;
            else {
                RoomIdPojo roomDetails = new RoomIdPojo();
                roomDetails.setFirstUserId(userID1);
                roomDetails.setSecondUserId(userID2);
                roomDetails.setTimeofCreation(LocalDateTime.now());
                roomIdRepo.save(roomDetails);
                return true;
            }
        }catch(Exception e){

            return false;
        }
    }
    public Long getRoomID(String userId1,String userId2){
        RoomIdPojo roomID=roomIdRepo.findByFirstUserIdAndSecondUserId(userId1,userId2);
        return roomID.getRoomId();
    }
    public String getRecieverID(Long roomID,String UserID){
        RoomIdPojo roomId=roomIdRepo.findByRoomId(roomID);
        if(roomId.getFirstUserId().equals(UserID)) return roomId.getSecondUserId();
        else return roomId.getFirstUserId();
    }
}