package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 1000)
    private String explanationFileUrl;  // 챗봇용 설명 파일 PDF
}
