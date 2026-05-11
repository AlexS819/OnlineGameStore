package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.UserRegistrationDTO;
import com.sochka.onlinegamestore.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Identity provisioning coordinator enforcing strict field constraints on initial registration streams.
 */
@Component
@RequiredArgsConstructor
public class RegistrationViewModel {

    private final UserService userService;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    public void clear() {
        name.set("");
        email.set("");
        password.set("");
        errorMessage.set("");
    }

    /**
     * Orchestrates new secure container provisioning triggering server registration.
     */
    public boolean performRegistration() {
        try {
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

            UserRegistrationDTO dto = UserRegistrationDTO.builder()
                    .name(name.get())
                    .email(email.get())
                    .password(password.get())
                    .build();

            userService.registerUser(dto);
            return true;
            
        } catch (Exception e) {
            errorMessage.set("Registration lock: " + e.getMessage());
            return false;
        }
    }
}
