package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                // ex. 빈센트 반 고흐

    @Column(length = 2000)
    private String bio;                // 작가 소개

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artwork> artworks;

    private String profileImage;       // 이미지 경로 (ex. /uploads/artists/van-gogh.jpg)

    private String nationality;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @OneToMany(mappedBy = "artist")
    private List<Exhibition> exhibitions; // 선택: 역방향 매핑
}
