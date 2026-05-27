package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.UserDTO;
import com.sochka.onlinegamestore.infrastructure.EmailSender;
import com.sochka.onlinegamestore.repository.UserRepository;
import com.sochka.onlinegamestore.service.UserService;
import com.sochka.onlinegamestore.ui.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Dynamic integration entity facilitating bi-directional state synchronization for authorization
 * nodes.
 */
@Component
@RequiredArgsConstructor
public class LoginViewModel {

    private final UserService userService;
    private final UserSession session;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    // Observed properties mapped directly to graphical input nodes
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty feedbackMessage = new SimpleStringProperty("");

    private String generatedResetCode;
    private UserDTO pendingUser;
    private String generated2faCode;

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty feedbackMessageProperty() {
        return feedbackMessage;
    }

    public boolean isTwoFactorRequired() {
        return pendingUser != null;
    }

    /**
     * Performs authentication cycle handing error handling feedback loops.
     */
    public boolean attemptLogin() {
        try {
            feedbackMessage.set("Verifying signature...");
            pendingUser = null;
            generated2faCode = null;
            
            UserDTO user = userService.authenticate(email.get(), password.get());
            
            if (user.isTwoFactorEnabled() && !"admin@gamestore.com".equalsIgnoreCase(user.getEmail())) {
                pendingUser = user;
                
                // Generate 2FA code
                java.util.Random random = new java.util.Random();
                generated2faCode = String.format("%06d", random.nextInt(1000000));
                
                // Send email
                emailSender.send(user.getEmail(),
                        "Online Game Store - Two-Factor Authentication Code",
                        "Hello " + user.getName() + ",\n\n" +
                        "Your 6-digit two-factor authentication code is: " + generated2faCode + "\n\n" +
                        "Please enter this code in the application window to complete your login.");
                
                feedbackMessage.set("Two-factor authentication code sent to your email.");
                return false;
            } else {
                // Cache active user identity for downstream privilege checks
                session.setCurrentUser(user);
                feedbackMessage.set("Welcome back, " + user.getName() + "!");
                return true;
            }
        } catch (Exception e) {
            feedbackMessage.set("Authentication failure: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if entered 2FA code is valid and signs in the user.
     */
    public boolean completeTwoFactorLogin(String enteredCode) {
        if (pendingUser == null || generated2faCode == null) {
            feedbackMessage.set("No pending 2FA login session.");
            return false;
        }
        if (enteredCode == null || !enteredCode.trim().equals(generated2faCode)) {
            feedbackMessage.set("Incorrect 2FA code. Please try again.");
            return false;
        }
        
        session.setCurrentUser(pendingUser);
        feedbackMessage.set("Welcome back, " + pendingUser.getName() + "!");
        pendingUser = null;
        generated2faCode = null;
        return true;
    }

    /**
     * Verifies user email and dispatches a password recovery 6-digit confirmation code.
     */
    public boolean sendResetCode(String emailAddress) {
        try {
            if (emailAddress == null || emailAddress.trim().isEmpty()) {
                feedbackMessage.set("Please enter a valid email address.");
                return false;
            }

            String targetEmail = emailAddress.trim();

            if ("admin@gamestore.com".equalsIgnoreCase(targetEmail)) {
                feedbackMessage.set("Password reset is not permitted for administrator accounts.");
                return false;
            }

            var optUser = userRepository.findByEmail(targetEmail);
            if (optUser.isEmpty()) {
                feedbackMessage.set("User with this email not found.");
                return false;
            }

            if (optUser.get().getRole() == com.sochka.onlinegamestore.domain.UserRole.ADMIN) {
                feedbackMessage.set("Password reset is not permitted for administrator accounts.");
                return false;
            }

            // Generate 6-digit random code
            java.util.Random random = new java.util.Random();
            generatedResetCode = String.format("%06d", random.nextInt(1000000));

            // Dispatch SMTP mail
            emailSender.send(targetEmail,
                  "Online Game Store - Password Reset Verification",
                  "Hello " + optUser.get().getName() + ",\n\n" +
                        "A password recovery sequence has been initiated for your account.\n" +
                        "Your 6-digit verification code is: " + generatedResetCode + "\n\n" +
                        "Please enter this code in the application window to specify a new password.");

            feedbackMessage.set("Verification code dispatched to your email address.");
            return true;
        } catch (Exception e) {
            feedbackMessage.set("Reset initiation failure: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if entered code matches.
     */
    public boolean verifyResetCode(String enteredCode) {
        if (generatedResetCode == null) {
            feedbackMessage.set("No reset session active.");
            return false;
        }
        if (enteredCode == null || !enteredCode.trim().equals(generatedResetCode)) {
            feedbackMessage.set("Invalid verification code.");
            return false;
        }
        return true;
    }

    /**
     * Submits the password update command to User Service.
     */
    public boolean completePasswordReset(String emailAddress, String newPassword) {
        try {
            if (newPassword == null || newPassword.length() < 6) {
                feedbackMessage.set("Password must be at least 6 characters long.");
                return false;
            }

            userService.resetPassword(emailAddress.trim(), newPassword);
            feedbackMessage.set("Password successfully updated. You may now authenticate.");
            generatedResetCode = null;
            return true;
        } catch (Exception e) {
            feedbackMessage.set("Password update failure: " + e.getMessage());
            return false;
        }
    }
}
