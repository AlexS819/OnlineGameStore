package com.sochka.onlinegamestore.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Enterprise-grade SMTP implementation of EmailSender, utilizing JavaMailSender.
 * Annotated with @Primary to automatically override ConsoleEmailSender when Spring starts.
 */
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(String toEmail, String subject, String content) {
        log.info("Preparing to send real SMTP email to {}", toEmail);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("SMTP email successfully dispatched to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send SMTP email to {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Email delivery failed: " + e.getMessage(), e);
        }
    }
}
