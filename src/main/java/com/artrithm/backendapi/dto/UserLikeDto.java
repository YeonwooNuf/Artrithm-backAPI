package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLikeDto {
    private Long userId;
    private Long exhibitionId;
    private Boolean liked; // ✔️ 요청 시 null로 보내도 됨
}
