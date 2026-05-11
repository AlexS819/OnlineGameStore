package com.sochka.onlinegamestore.domain;

public enum KeyStatus {
    AVAILABLE("Available"),
    SOLD("Sold"),
    REDEEMED("Redeemed");

    private final String displayName;

    KeyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
