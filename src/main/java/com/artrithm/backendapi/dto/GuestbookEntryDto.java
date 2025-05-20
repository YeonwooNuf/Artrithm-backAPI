package com.artrithm.backendapi.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookEntryDto {
    private Long id;
    private Long userId;
    private String nickname;
    private String message;
    private String createdAt;       // ✅ 선택
    private String profileImage;    // ✅ 선택
}
