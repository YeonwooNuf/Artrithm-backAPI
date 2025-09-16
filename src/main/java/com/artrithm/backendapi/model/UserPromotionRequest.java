package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class UserPromotionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String reason;

    @ElementCollection
    private List<String> artworkImagePaths; // 이미지 경로만 저장

    private boolean approved;

    private LocalDateTime createdAt;
}
