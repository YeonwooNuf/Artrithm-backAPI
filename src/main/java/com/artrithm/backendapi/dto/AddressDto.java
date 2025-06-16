package com.artrithm.backendapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private Long id;
    private String zonecode;
    private String roadAddress;
    private String jibunAddress;
    private String detailAddress;
    private String reference;
    private boolean isDefault;
}