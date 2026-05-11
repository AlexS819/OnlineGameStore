package com.sochka.onlinegamestore.infrastructure;

/**
 * Notification transport mechanism for client communications.
 */
public interface EmailSender {
    void send(String toEmail, String subject, String content);
}
