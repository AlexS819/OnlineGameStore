package com.sochka.onlinegamestore.exception;

/**
 * Unified standard runtime hierarchy for all business-related anomalies.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
