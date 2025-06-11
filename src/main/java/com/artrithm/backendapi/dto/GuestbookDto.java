package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Guestbook;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestbookDto {
    private Long id;
    private Long userId;         // 요청용
    private String nickname;     // 응답용
    private String profileImage; // 사용자 프로필 이미지
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Entity → DTO 변환
    public static GuestbookDto fromEntity(Guestbook guestbook) {
        return GuestbookDto.builder()
                .id(guestbook.getId())
                .userId(guestbook.getUser().getId())
                .nickname(guestbook.getUser().getNickname())
                .profileImage(guestbook.getUser().getProfileImage())
                .content(guestbook.getContent())
                .createdAt(guestbook.getCreatedAt())
                .updatedAt(guestbook.getUpdatedAt())
                .build();
    }
}
