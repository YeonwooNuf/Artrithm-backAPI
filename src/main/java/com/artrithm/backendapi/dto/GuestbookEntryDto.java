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
    private String nickname;  // ✅ 이거 포함
    private String message;
}
