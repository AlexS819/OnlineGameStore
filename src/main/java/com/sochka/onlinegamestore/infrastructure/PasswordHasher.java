package com.sochka.onlinegamestore.infrastructure;

/**
 * Secure cryptographic hashing protocol for authentication logic.
 */
public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
