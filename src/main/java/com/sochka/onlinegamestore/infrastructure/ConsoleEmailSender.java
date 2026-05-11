package com.sochka.onlinegamestore.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simulation adapter documenting system transmissions via application diagnostics logs.
 */
@Component
@Slf4j
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void send(String toEmail, String subject, String content) {
        log.info("---------------------------------------------------------");
        log.info("SIMULATING EMAIL DISPATCH:");
        log.info("TO: {}", toEmail);
        log.info("SUBJECT: {}", subject);
        log.info("CONTENT: {}", content);
        log.info("---------------------------------------------------------");
    }
}
