package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedPriceArtistGroupDto {
    private Long artistId;
    private String artistName;
    private String artistProfileImage;
    private String artistBio;

    private List<FixedPriceArtworkDto> works = new ArrayList<>();
}

