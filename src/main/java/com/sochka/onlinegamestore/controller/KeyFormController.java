package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.viewmodel.KeyFormViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyFormController {

    private final KeyFormViewModel viewModel;

    @FXML private TextField keyField;
    @FXML private ComboBox<GameDTO> gameCombo;
    @FXML private Label errorLabel;
    
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private boolean saveConfirmed = false;
    public boolean isSaveConfirmed() { return saveConfirmed; }

    @FXML
    public void initialize() {
        viewModel.loadDependencies();
        
        keyField.textProperty().bindBidirectional(viewModel.keyValueProperty());
        gameCombo.setItems(viewModel.getGames());
        gameCombo.valueProperty().bindBidirectional(viewModel.selectedGameProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> closeDialog());
    }

    public void loadForEdit(ActivationKeyDTO key) {
        viewModel.loadEditingState(key);
    }

    private void handleSave() {
        if (viewModel.saveKey()) {
            saveConfirmed = true;
            closeDialog();
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
