package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.ui.SceneSwitcher;
import com.sochka.onlinegamestore.viewmodel.RegistrationViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Enrollment flow administrator responsible for initial identity onboarding sequences.
 */
@Component
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationViewModel viewModel;
    private final SceneSwitcher sceneSwitcher;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    @FXML private Button registerBtn;
    @FXML private Button backToLoginBtn;

    @FXML
    public void initialize() {
        viewModel.clear();
        
        // Bind state
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        // Wire actions
        registerBtn.setOnAction(e -> handleRegistration());
        backToLoginBtn.setOnAction(e -> switchToLogin());
    }

    private void handleRegistration() {
        if (viewModel.sendVerificationCode()) {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Email Verification");
            dialog.setHeaderText("Verification Code Sent");
            dialog.setContentText("Please enter the 6-digit confirmation code sent to " + emailField.getText() + ":");
            
            java.util.Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String code = result.get();
                if (viewModel.completeRegistration(code)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Successful");
                    alert.setHeaderText("Welcome to Online Game Store!");
                    alert.setContentText("Account initialization finished successfully. You may now authenticate.");
                    alert.showAndWait();
                    
                    switchToLogin();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Verification Failed");
                    alert.setHeaderText("Incorrect Code");
                    alert.setContentText(viewModel.errorMessageProperty().get());
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setHeaderText("Validation Failed");
            alert.setContentText(viewModel.errorMessageProperty().get());
            alert.showAndWait();
        }
    }

    private void switchToLogin() {
        Stage stage = (Stage) registerBtn.getScene().getWindow();
        sceneSwitcher.switchScene(stage, "/views/login.fxml", "Game Store - Authorization", 800, 600);
    }
}
