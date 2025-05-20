package com.artrithm.backendapi.model;

import jakarta.persistence.*;

@Entity
public class GuestbookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private Exhibition exhibition;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User writer;

    private String message;
}
