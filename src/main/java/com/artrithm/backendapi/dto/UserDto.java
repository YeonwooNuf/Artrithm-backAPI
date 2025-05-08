package com.artrithm.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에 포함하지 않음
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;              // 응답용
    private String loginId;       // 입력/출력 모두 사용 가능
    private String password;      // 입력용
    private String nickname;
    private String email;
    private String phoneNumber;
}
