package com.artrithm.backendapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentTargetType {
    AUCTION_ORDER,
    FIXED_ORDER,
    SUBSCRIPTION,
    PENALTY;

    @JsonCreator
    public static PaymentTargetType fromString(String value) {
        return switch (value.toUpperCase()) {
            case "FIXED_PRICE" -> FIXED_ORDER;
            case "AUCTION" -> AUCTION_ORDER;
            case "SUBSCRIPTION" -> SUBSCRIPTION;
            case "PENALTY" -> PENALTY;
            default -> throw new IllegalArgumentException("Unknown value: " + value);
        };
    }
}
