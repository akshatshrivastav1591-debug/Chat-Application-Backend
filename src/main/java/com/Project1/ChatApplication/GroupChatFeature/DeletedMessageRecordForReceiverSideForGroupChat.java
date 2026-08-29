package com.Project1.ChatApplication.GroupChatFeature;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DeletedMessageRecordForReceiverSideForGroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long indexing;
    private String deletedMessageId;
    private String deletedForReceiverUserId;
}
