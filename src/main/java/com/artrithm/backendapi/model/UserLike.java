package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exhibition exhibition;

    public UserLike(User user, Exhibition exhibition) {
        this.user = user;
        this.exhibition = exhibition;
    }

    protected UserLike() {} // JPA 기본 생성자
}
