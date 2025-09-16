package com.artrithm.backendapi.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPromotionRequestDto {

    // 📤 사용자 요청 시 사용
    private Long userId;
    private String reason;
    private List<MultipartFile> artworkImages;  // 업로드용 (Request)

    // 📥 관리자 응답 시 사용
    private Long requestId;
    private String nickname;
    private List<String> artworkImageUrls;      // 응답용 (Response)
    private boolean approved;
    private LocalDateTime createdAt;

    // 유연하게 사용할 수 있도록 일부 필드만 null 허용
}
