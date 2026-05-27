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
    @FXML private Button forgotPasswordBtn;

    @FXML
    public void initialize() {
        // Establish real-time dynamic data bonds between layout nodes and model properties
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        messageLabel.textProperty().bind(viewModel.feedbackMessageProperty());
        
        // Wire execute flow triggering action
        loginBtn.setOnAction(event -> handleLoginClick());
        goToRegisterBtn.setOnAction(event -> handleSwitchToRegister());
        forgotPasswordBtn.setOnAction(event -> handleForgotPassword());
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
             if (viewModel.isTwoFactorRequired()) {
                 messageLabel.setStyle("-fx-text-fill: #1877F2;"); // Nice soft blue for informative messages instead of scary red!
                 javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
                 dialog.setTitle("Two-Factor Authentication");
                 dialog.setHeaderText("2FA Verification Required");
                 dialog.setContentText("Please enter the 6-digit security code sent to your email address:");
                 
                 java.util.Optional<String> result = dialog.showAndWait();
                 if (result.isPresent()) {
                     String code = result.get().trim();
                     if (viewModel.completeTwoFactorLogin(code)) {
                         messageLabel.setStyle("-fx-text-fill: #2ECC71;");
                         Stage stage = (Stage) loginBtn.getScene().getWindow();
                         sceneSwitcher.switchScene(stage, "/views/dashboard.fxml", "Game Store - Dashboard", 1100, 700);
                     } else {
                         messageLabel.setStyle("-fx-text-fill: #E74C3C;");
                         showError("Verification Failed", viewModel.feedbackMessageProperty().get());
                     }
                 }
             } else {
                 messageLabel.setStyle("-fx-text-fill: #E74C3C;");
             }
        }
    }

    private void handleForgotPassword() {
        javafx.scene.control.TextInputDialog emailDialog = new javafx.scene.control.TextInputDialog();
        emailDialog.setTitle("Password Recovery");
        emailDialog.setHeaderText("Reset Password Request");
        emailDialog.setContentText("Please enter your registered email address:");

        java.util.Optional<String> emailResult = emailDialog.showAndWait();
        if (emailResult.isPresent()) {
            String emailInput = emailResult.get().trim();
            if (emailInput.isEmpty()) {
                showError("Error", "Email address cannot be empty.");
                return;
            }

            if (viewModel.sendResetCode(emailInput)) {
                // Code sent successfully, prompt for verification code
                javafx.scene.control.TextInputDialog codeDialog = new javafx.scene.control.TextInputDialog();
                codeDialog.setTitle("Password Recovery");
                codeDialog.setHeaderText("Verification Code Sent");
                codeDialog.setContentText("Please enter the 6-digit verification code sent to " + emailInput + ":");

                java.util.Optional<String> codeResult = codeDialog.showAndWait();
                if (codeResult.isPresent()) {
                    String codeInput = codeResult.get().trim();
                    if (viewModel.verifyResetCode(codeInput)) {
                        // Code is valid, prompt for new password
                        javafx.scene.control.TextInputDialog passwordDialog = new javafx.scene.control.TextInputDialog();
                        passwordDialog.setTitle("Password Recovery");
                        passwordDialog.setHeaderText("Set New Password");
                        passwordDialog.setContentText("Enter your new password (minimum 6 characters):");

                        java.util.Optional<String> passwordResult = passwordDialog.showAndWait();
                        if (passwordResult.isPresent()) {
                            String newPasswordInput = passwordResult.get().trim();
                            if (viewModel.completePasswordReset(emailInput, newPasswordInput)) {
                                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText("Password Reset Successful");
                                alert.setContentText("Your password has been successfully updated. You may now log in.");
                                alert.showAndWait();
                            } else {
                                showError("Reset Failed", viewModel.feedbackMessageProperty().get());
                            }
                        }
                    } else {
                        showError("Verification Failed", viewModel.feedbackMessageProperty().get());
                    }
                }
            } else {
                showError("Request Failed", viewModel.feedbackMessageProperty().get());
            }
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
