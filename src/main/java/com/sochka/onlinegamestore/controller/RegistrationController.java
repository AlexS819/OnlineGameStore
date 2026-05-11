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
        if (viewModel.performRegistration()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Verified");
            alert.setHeaderText("Welcome to Online Game Store!");
            alert.setContentText("Account initialization finished successfully. You may now authenticate.");
            alert.showAndWait();
            
            switchToLogin();
        }
    }

    private void switchToLogin() {
        Stage stage = (Stage) registerBtn.getScene().getWindow();
        sceneSwitcher.switchScene(stage, "/views/login.fxml", "Game Store - Authorization", 800, 600);
    }
}
