package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.viewmodel.GameFormViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Interface manager handling discrete modal containment and flow execution for content creation.
 */
@Component
@RequiredArgsConstructor
public class GameFormController {

    private final GameFormViewModel viewModel;

    @FXML private TextField titleField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;
    @FXML private ComboBox<PublisherDTO> publisherCombo;
    @FXML private ListView<com.sochka.onlinegamestore.dto.GenreDTO> genreListView;
    @FXML private Label errorLabel;
    
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    // Public tracking mechanism for tracking outcome
    private boolean saveConfirmed = false;
    public boolean isSaveConfirmed() { return saveConfirmed; }

    @FXML
    public void initialize() {
        // Pre-flight requirements loading
        viewModel.loadDependencies();
        
        // Establish hard bindings between native controls and state
        titleField.textProperty().bindBidirectional(viewModel.titleProperty());
        priceField.textProperty().bindBidirectional(viewModel.priceProperty());
        descriptionArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        imageUrlField.textProperty().bindBidirectional(viewModel.imageUrlProperty());
        publisherCombo.setItems(viewModel.getPublishers());
        publisherCombo.setConverter(new javafx.util.StringConverter<PublisherDTO>() {
            @Override
            public String toString(PublisherDTO object) {
                return object == null ? "" : object.getName();
            }

            @Override
            public PublisherDTO fromString(String string) {
                return null;
            }
        });
        publisherCombo.valueProperty().bindBidirectional(viewModel.selectedPublisherProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        genreListView.setItems(viewModel.getGenres());
        genreListView.setCellFactory(lv -> new javafx.scene.control.ListCell<com.sochka.onlinegamestore.dto.GenreDTO>() {
            @Override
            protected void updateItem(com.sochka.onlinegamestore.dto.GenreDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Listen to selection changes to update view model
        genreListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.getSelectedGenreIds().clear();
            for (com.sochka.onlinegamestore.dto.GenreDTO g : genreListView.getSelectionModel().getSelectedItems()) {
                viewModel.getSelectedGenreIds().add(g.getId());
            }
        });
        
        // Assign discrete flow handlers
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> closeDialog());
    }

    public void loadForEdit(com.sochka.onlinegamestore.dto.GameDTO game) {
        viewModel.loadEditingState(game);
        
        // Sync visual list view with the state
        genreListView.getSelectionModel().clearSelection();
        if (game.getGenreIds() != null) {
            for (com.sochka.onlinegamestore.dto.GenreDTO g : genreListView.getItems()) {
                if (game.getGenreIds().contains(g.getId())) {
                    genreListView.getSelectionModel().select(g);
                }
            }
        }
    }

    private void handleSave() {
        if (viewModel.saveGame()) {
            saveConfirmed = true;
            closeDialog();
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
