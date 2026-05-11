package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.UserDTO;
import com.sochka.onlinegamestore.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Dynamic integration entity facilitating bi-directional state synchronization for authorization nodes.
 */
@Component
@RequiredArgsConstructor
public class LoginViewModel {

    private final com.sochka.onlinegamestore.service.UserService userService;
    private final com.sochka.onlinegamestore.ui.UserSession session;

    // Observed properties mapped directly to graphical input nodes
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty feedbackMessage = new SimpleStringProperty("");

    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty feedbackMessageProperty() { return feedbackMessage; }

    /**
     * Performs authentication cycle handing error handling feedback loops.
     */
    public boolean attemptLogin() {
        try {
            feedbackMessage.set("Verifying signature...");
            
            UserDTO user = userService.authenticate(email.get(), password.get());
            
            // Cache active user identity for downstream privilege checks
            session.setCurrentUser(user);
            
            feedbackMessage.set("Welcome back, " + user.getName() + "!");
            return true;
        } catch (Exception e) {
            feedbackMessage.set("Authentication failure: " + e.getMessage());
            return false;
        }
    }
}
