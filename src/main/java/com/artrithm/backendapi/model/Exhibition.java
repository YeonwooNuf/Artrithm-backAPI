package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exhibitions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private String title;

    @Column(length = 1000)
    private String description;

    private String theme;

    @Column(nullable = false)
    private String thumbnailUrl;

    @ElementCollection
    @CollectionTable(name = "exhibition_keywords", joinColumns = @JoinColumn(name = "exhibition_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id") // Nullable: 명화 전시에만 설정됨
    private Artist artist;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artwork> artworks;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guestbook> guestbook;
}
