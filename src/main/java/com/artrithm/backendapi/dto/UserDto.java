package com.artrithm.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

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
    private LocalDate birth;
    private String email;
    private String phoneNumber;

    private String role;               // USER / ARTIST / ADMIN
    private Boolean isArtistApproved; // 작가 승인 여부
    private String artistBio;         // 작가 소개글
    private String profileImage;      // S3 경로 또는 URL

    private MultipartFile profileImageFile; // ✅ 실제 이미지 파일
}
