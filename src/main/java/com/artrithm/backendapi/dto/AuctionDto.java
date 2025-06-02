package com.artrithm.backendapi.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionDto {
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer startPrice;

    private String winnerUserId;
    private Integer finalPrice;

    private ArtworkDto artwork;
    private String winnerNickname;
}
