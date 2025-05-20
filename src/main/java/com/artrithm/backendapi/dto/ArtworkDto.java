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
    private String imageUrl;  // 변경: src → imageUrl
}
