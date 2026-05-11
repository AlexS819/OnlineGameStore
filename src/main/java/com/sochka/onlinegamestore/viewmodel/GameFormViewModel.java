package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.dto.GenreDTO;
import com.sochka.onlinegamestore.service.GameService;
import com.sochka.onlinegamestore.service.GenreService;
import com.sochka.onlinegamestore.service.PublisherService;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Transactional input negotiator validating data payloads before catalog enrollment.
 */
@Component
@RequiredArgsConstructor
public class GameFormViewModel {

    private final GameService gameService;
    private final PublisherService publisherService;
    private final GenreService genreService;


    // Form inputs
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty price = new SimpleStringProperty("");
    private final ObjectProperty<PublisherDTO> selectedPublisher = new SimpleObjectProperty<>();
    private final java.util.Set<java.util.UUID> selectedGenreIds = new java.util.HashSet<>();
    
    // Identification state to distinguish between new creation and update
    private java.util.UUID editingGameId = null; 
    
    // Visual options
    private final ObservableList<PublisherDTO> publishers = FXCollections.observableArrayList();
    private final ObservableList<GenreDTO> genres = FXCollections.observableArrayList();
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public StringProperty titleProperty() { return title; }
    public StringProperty priceProperty() { return price; }
    public ObjectProperty<PublisherDTO> selectedPublisherProperty() { return selectedPublisher; }
    public ObservableList<PublisherDTO> getPublishers() { return publishers; }
    public ObservableList<GenreDTO> getGenres() { return genres; }
    public java.util.Set<java.util.UUID> getSelectedGenreIds() { return selectedGenreIds; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    public void loadDependencies() {
        publishers.clear();
        publishers.addAll(publisherService.findAll());
        genres.clear();
        genres.addAll(genreService.findAll());
        
        // Clear form state for reuse
        title.set("");
        price.set("");
        selectedPublisher.set(null);
        selectedGenreIds.clear();

        errorMessage.set("");
        editingGameId = null;
    }

    public void loadEditingState(com.sochka.onlinegamestore.dto.GameDTO game) {
        loadDependencies(); // ensure publishers loaded
        title.set(game.getTitle());
        price.set(game.getPrice().toString());
        editingGameId = game.getId();
        
        // Find the matching publisher in the observable list to pre-select it
        publishers.stream()
            .filter(p -> p.getName().equals(game.getPublisherName()))
            .findFirst()
            .ifPresent(selectedPublisher::set);
            
        selectedGenreIds.clear();
        if (game.getGenreIds() != null) {
            selectedGenreIds.addAll(game.getGenreIds());
        }
    }

    public boolean saveGame() {
        try {
            // Basic validations satisfy Day 9 "Validation" requirements
            if (title.get() == null || title.get().trim().isEmpty()) {
                errorMessage.set("Please specify a product title.");
                return false;
            }
            
            if (selectedPublisher.get() == null) {
                errorMessage.set("Select a publisher from registry.");
                return false;
            }

            BigDecimal finalPrice;
            try {
                finalPrice = new BigDecimal(price.get().replace(",", "."));
                if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                errorMessage.set("Price must be a positive numeral.");
                return false;
            }

            // Commit dynamic transaction based on operational state
            if (editingGameId != null) {
                gameService.updateGame(editingGameId, title.get(), finalPrice, selectedPublisher.get().getId(), selectedGenreIds);
            } else {
                gameService.createGame(title.get(), finalPrice, selectedPublisher.get().getId(), selectedGenreIds);
            }
            return true;

        } catch (Exception ex) {
            errorMessage.set("Database rejection: " + ex.getMessage());
            return false;
        }
    }
}
