package com.Project1.ChatApplication.ChatFeature.Websocket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChatsDetailsPojo {
    @Id
    private Long roomId;
    private  String UserId1;
    private  String UserId2;
    private  String lastMessage;
    @Column(name = "lasttmessagesendingdate")
    private LocalDate sendingDate;
    @Column(name="lastmessagesendingtime")
    private LocalTime sendingTime;
    @Column(name="user1uncheckedmessages")
    private  Long user1UnseenMessageCount;
    @Column(name="user2uncheckedmessages")
    private  Long user2UnseenMessageCount;
    @Column(name = "isblocked")
    private  boolean blocked;
    @Column(name = "blockedby")
    private String blockedBY;
}
