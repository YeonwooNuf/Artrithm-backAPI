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
    private Long userId;     // 요청용
    private String nickname; // 응답용
    private String content;
    private LocalDateTime createdAt;

    public static GuestbookDto from(Guestbook g) {
        return GuestbookDto.builder()
                .id(g.getId())
                .userId(g.getUser().getId()) // 필요시
                .nickname(g.getUser().getNickname())
                .content(g.getContent())
                .createdAt(g.getCreatedAt())
                .build();
    }
}

