package com.artrithm.backendapi.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    private String id; // MongoDB 자동 생성 ID

    private Long exhibitionId; // 어떤 전시인지
    private Long artistId;     // 작가 ID
    private Long viewerId;     // 관람자 ID

    private LocalDateTime createdAt;
}
