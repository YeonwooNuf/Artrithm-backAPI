package com.artrithm.backendapi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private String roomId;             // 채팅방 ID
    private String otherNickname;      // 상대방 닉네임
    private String otherProfileImage;  // 상대방 프로필 이미지
    private String lastMessage;        // 마지막 메시지 내용
    private LocalDateTime lastMessageTime; // 마지막 메시지 시간
}
