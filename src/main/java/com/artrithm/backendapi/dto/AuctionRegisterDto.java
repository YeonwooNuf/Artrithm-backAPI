package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRegisterDto {
    private Long auctionRequestId;
    private LocalDateTime endTime;
}
