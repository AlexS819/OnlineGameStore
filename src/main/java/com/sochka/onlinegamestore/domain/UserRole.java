package com.sochka.onlinegamestore.domain;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private final String name;

    private UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}