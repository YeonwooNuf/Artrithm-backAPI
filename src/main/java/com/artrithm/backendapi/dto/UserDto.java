package com.artrithm.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String loginId;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;

    private String role;               // USER / ARTIST / ADMIN
    private Boolean isArtistApproved; // 작가 승인 여부
    private String artistBio;         // 작가 소개글
    private String profileImage;      // 사용자 공통 프로필 이미지
}
