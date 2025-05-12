package com.artrithm.backendapi.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Exhibition {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User author;  // 작성자

    private String title;
    private String description;
    private String theme;
    private String thumbnail;

    @ElementCollection
    private List<String> keywords;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
    private List<Artwork> artworks;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
    private List<GuestbookEntry> guestbook;
}

