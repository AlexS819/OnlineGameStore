package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.service.ActivationKeyService;
import com.sochka.onlinegamestore.service.GameService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KeyFormViewModel {

    private final ActivationKeyService keyService;
    private final GameService gameService;

    private final StringProperty keyValue = new SimpleStringProperty("");
    private final ObjectProperty<GameDTO> selectedGame = new SimpleObjectProperty<>();
    
    private final ObservableList<GameDTO> games = FXCollections.observableArrayList();
    private final StringProperty errorMessage = new SimpleStringProperty("");
    
    private UUID editingKeyId = null;

    public StringProperty keyValueProperty() { return keyValue; }
    public ObjectProperty<GameDTO> selectedGameProperty() { return selectedGame; }
    public ObservableList<GameDTO> getGames() { return games; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    public void loadDependencies() {
        games.clear();
        games.addAll(gameService.findAll());
        
        keyValue.set("");
        selectedGame.set(null);
        errorMessage.set("");
        editingKeyId = null;
    }

    public void loadEditingState(ActivationKeyDTO key) {
        loadDependencies();
        keyValue.set(key.getKeyValue());
        editingKeyId = key.getId();
        
        if (key.getGameId() != null) {
            games.stream()
                .filter(g -> g.getId().equals(key.getGameId()))
                .findFirst()
                .ifPresent(selectedGame::set);
        }
    }

    public boolean saveKey() {
        try {
            String rawKey = keyValue.get() != null ? keyValue.get().trim().toUpperCase() : "";
            
            if (rawKey.isEmpty()) {
                errorMessage.set("Please enter the activation code.");
                return false;
            }
            
            if (rawKey.length() < 5) {
                errorMessage.set("Activation key looks too short (min 5 chars).");
                return false;
            }

            // Simple alphanumeric and hyphen validator
            if (!rawKey.matches("^[A-Z0-9\\-]+$")) {
                errorMessage.set("Code contains invalid symbols. Use letters, numbers, and dashes only.");
                return false;
            }

            if (selectedGame.get() == null) {
                errorMessage.set("You must bind the key to a specific game.");
                return false;
            }

            if (editingKeyId != null) {
                keyService.updateKey(editingKeyId, selectedGame.get().getId(), rawKey);
            } else {
                keyService.addKeyToGame(selectedGame.get().getId(), rawKey);
            }
            return true;
        } catch (Exception ex) {
            errorMessage.set("Operation failed: " + ex.getMessage());
            return false;
        }
    }
}
