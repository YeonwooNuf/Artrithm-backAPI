package com.artrithm.backendapi.dto;


import com.artrithm.backendapi.model.Artwork;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRequestDto {
    private Long requestId;
    private Long artworkId;
    private int startPrice;
}
