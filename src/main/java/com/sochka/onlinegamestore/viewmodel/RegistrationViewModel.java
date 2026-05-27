package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.UserRegistrationDTO;
import com.sochka.onlinegamestore.service.UserService;
import com.sochka.onlinegamestore.repository.UserRepository;
import com.sochka.onlinegamestore.infrastructure.EmailSender;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Random;

/**
 * Identity provisioning coordinator enforcing strict field constraints on initial registration streams.
 */
@Component
@RequiredArgsConstructor
public class RegistrationViewModel {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private String generatedCode;

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    public void clear() {
        name.set("");
        email.set("");
        password.set("");
        errorMessage.set("");
        generatedCode = null;
    }

    /**
     * Validates input fields.
     */
    public boolean validateFields() {
        if (name.get().isBlank() || email.get().isBlank() || password.get().isBlank()) {
            errorMessage.set("Complete all required credentials.");
            return false;
        }
        
        // Strict email format validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.get().matches(emailRegex)) {
            errorMessage.set("Provide a legitimate email format (e.g. user@example.com).");
            return false;
        }
        
        // Strict password length validation
        if (password.get().length() < 6) {
            errorMessage.set("Password must be at least 6 characters long.");
            return false;
        }

        // Check if user already exists
        if (userRepository.existsByEmail(email.get())) {
            errorMessage.set("Account with this email already registered.");
            return false;
        }

        errorMessage.set("");
        return true;
    }

    /**
     * Generates a 6-digit code and emails it to the user.
     */
    public boolean sendVerificationCode() {
        try {
            if (!validateFields()) {
                return false;
            }

            // Generate 6-digit code
            Random random = new Random();
            generatedCode = String.format("%06d", random.nextInt(1000000));

            // Send SMTP email
            emailSender.send(email.get(),
                    "Online Game Store - Registration Verification",
                    "Hello " + name.get() + ",\n\n" +
                    "Thank you for registering at Online Game Store!\n" +
                    "Your 6-digit confirmation code is: " + generatedCode + "\n\n" +
                    "Please enter this code in the application window to complete your registration.");

            return true;
        } catch (Exception e) {
            errorMessage.set("Failed to send verification code: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies the entered code and completes the actual registration.
     */
    public boolean completeRegistration(String enteredCode) {
        try {
            if (generatedCode == null) {
                errorMessage.set("No verification session active.");
                return false;
            }

            if (enteredCode == null || !enteredCode.trim().equals(generatedCode)) {
                errorMessage.set("Invalid verification code. Please check your email.");
                return false;
            }

            UserRegistrationDTO dto = UserRegistrationDTO.builder()
                    .name(name.get())
                    .email(email.get())
                    .password(password.get())
                    .build();

            userService.registerUser(dto);
            return true;

        } catch (Exception e) {
            errorMessage.set("Registration failed: " + e.getMessage());
            return false;
        }
    }
}
