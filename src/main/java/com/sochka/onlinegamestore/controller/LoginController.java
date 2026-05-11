package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.viewmodel.LoginViewModel;
import com.sochka.onlinegamestore.ui.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Native visual event coordinator governing structural transitions and input piping.
 */
@Component
@RequiredArgsConstructor
public class LoginController {

    private final LoginViewModel viewModel;
    private final SceneSwitcher sceneSwitcher;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button loginBtn;
    @FXML private Button goToRegisterBtn;

    @FXML
    public void initialize() {
        // Establish real-time dynamic data bonds between layout nodes and model properties
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        messageLabel.textProperty().bind(viewModel.feedbackMessageProperty());
        
        // Wire execute flow triggering action
        loginBtn.setOnAction(event -> handleLoginClick());
        goToRegisterBtn.setOnAction(event -> handleSwitchToRegister());
    }

    private void handleSwitchToRegister() {
        Stage stage = (Stage) goToRegisterBtn.getScene().getWindow();
        sceneSwitcher.switchScene(stage, "/views/registration.fxml", "Game Store - Create Account", 800, 600);
    }

    private void handleLoginClick() {
        boolean isSuccess = viewModel.attemptLogin();
        if (isSuccess) {
             messageLabel.setStyle("-fx-text-fill: #2ECC71;");
             
             // Retrieve current window stage
             Stage stage = (Stage) loginBtn.getScene().getWindow();
             
             // Seamless transition to main control panel
             sceneSwitcher.switchScene(stage, "/views/dashboard.fxml", "Game Store - Dashboard", 1100, 700);
        } else {
             messageLabel.setStyle("-fx-text-fill: #E74C3C;");
        }
    }
}
