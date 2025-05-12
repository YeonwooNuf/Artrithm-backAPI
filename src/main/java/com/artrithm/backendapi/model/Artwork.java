package com.artrithm.backendapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Artwork {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Exhibition exhibition;

    private String title;
    private String description;
    private String src;  // 이미지 경로
}

