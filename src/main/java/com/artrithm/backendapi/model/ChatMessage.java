package com.artrithm.backendapi.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;

    private String roomId;       // 어느 채팅방의 메시지인지 (ChatRoom의 id 참조)
    private Long senderId;       // 보낸 사람 ID
    private String senderRole;   // "artist" or "viewer" (UI 표시용)
    private String message;      // 내용
    private LocalDateTime sentAt;
}
