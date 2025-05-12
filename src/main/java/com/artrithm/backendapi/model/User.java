package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 고유 식별자

    @Column(nullable = false, unique = true)
    private String loginId;  // 회원가입용 ID

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // ✅ 사용자 권한 (기본값: USER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    public enum Role {
        USER, ARTIST, ADMIN
    }

    // ✅ 작가 승인 여부
    @Column(nullable = false)
    private boolean isArtistApproved = false;

    // ✅ 작가 설명 (승인 후 등록 가능)
    private String artistBio;

    // ✅ 프로필 이미지 경로 (가입 후 등록 가능)
    private String profileImage;
}
