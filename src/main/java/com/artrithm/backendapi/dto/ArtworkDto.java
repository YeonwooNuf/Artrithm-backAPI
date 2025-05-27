package com.artrithm.backendapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkDto {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;

    private String artistName; // 명화 작가일 경우
    private String userNickname; // 개인 작가일 경우
    private String explanationFileUrl; // 명화 설명용 PDF
}
