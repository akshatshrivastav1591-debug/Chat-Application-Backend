package com.Project1.ChatApplication.RoomID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


import java.time.LocalDateTime;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RoomIdPojo{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long roomId;
    private String firstUserId;
    private String secondUserId;
    private LocalDateTime timeofCreation;
}
