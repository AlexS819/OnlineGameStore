package com.sochka.onlinegamestore.viewmodel;

import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.service.GameService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.sochka.onlinegamestore.service.ActivationKeyService;
import com.sochka.onlinegamestore.service.OrderService;
import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.dto.OrderDTO;
import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.dto.GenreDTO;
import com.sochka.onlinegamestore.service.ActivationKeyService;
import com.sochka.onlinegamestore.service.OrderService;
import com.sochka.onlinegamestore.service.PublisherService;
import com.sochka.onlinegamestore.service.GenreService;
import com.sochka.onlinegamestore.infrastructure.ReportGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Command dispatcher consolidating product catalog operations and search telemetry.
 */
@Component
@RequiredArgsConstructor
public class DashboardViewModel {

    private final GameService gameService;
    private final ActivationKeyService keyService;
    private final OrderService orderService;
    private final com.sochka.onlinegamestore.service.UserService userService;
    private final PublisherService publisherService;
    private final GenreService genreService;
    private final ReportGenerator reportGenerator;

    // Observable collection synchronized directly to dynamic table
    private final ObservableList<GameDTO> gameList = FXCollections.observableArrayList();
    private final ObservableList<ActivationKeyDTO> keyList = FXCollections.observableArrayList();
    private final ObservableList<OrderDTO> orderList = FXCollections.observableArrayList();
    private final ObservableList<PublisherDTO> publisherList = FXCollections.observableArrayList();
    private final ObservableList<GenreDTO> genreList = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty("");

    public ObservableList<GameDTO> getGameList() { return gameList; }
    public ObservableList<ActivationKeyDTO> getKeyList() { return keyList; }
    public ObservableList<OrderDTO> getOrderList() { return orderList; }
    public ObservableList<PublisherDTO> getPublisherList() { return publisherList; }
    public ObservableList<GenreDTO> getGenreList() { return genreList; }
    public StringProperty searchQueryProperty() { return searchQuery; }

    private final java.util.Set<java.util.UUID> selectedPublisherIds = new java.util.HashSet<>();
    private final java.util.Set<java.util.UUID> selectedGenreIds = new java.util.HashSet<>();
    
    public java.util.Set<java.util.UUID> getSelectedPublisherIds() { return selectedPublisherIds; }
    public java.util.Set<java.util.UUID> getSelectedGenreIds() { return selectedGenreIds; }

    /**
     * Regenerates operational inventory lists reading currently loaded persistent caches.
     */
    public void loadAllGames() {
        List<GameDTO> list = gameService.findAll();
        gameList.clear();
        gameList.addAll(list);
    }

    /**
     * Filters active catalog based on current active semantic search string and selected dimensional filters.
     */
    public void performSearch() {
        String query = searchQuery.get() != null ? searchQuery.get().trim().toLowerCase() : "";

        // Start with the full persistent list
        List<GameDTO> allGames = gameService.findAll();
        
        List<GameDTO> filtered = allGames.stream().filter(g -> {
            boolean matchesQuery = query.isEmpty() || g.getTitle().toLowerCase().contains(query);
            
            boolean matchesPub = selectedPublisherIds.isEmpty() || 
                (g.getPublisherId() != null && selectedPublisherIds.contains(g.getPublisherId()));
                
            boolean matchesGenre = selectedGenreIds.isEmpty() || 
                (g.getGenreIds() != null && g.getGenreIds().stream().anyMatch(selectedGenreIds::contains));
                
            return matchesQuery && matchesPub && matchesGenre;
        }).collect(java.util.stream.Collectors.toList());

        gameList.clear();
        gameList.addAll(filtered);
    }

    /**
     * Commits removal workflow processing back into persistent architecture.
     */
    public void deleteSelectedGame(GameDTO game) {
        if (game != null) {
            gameService.deleteGame(game.getId());
            loadAllGames(); // Instantly re-sync display
        }
    }

    /**
     * Regenerates operational inventory lists reading currently loaded persistent caches.
     */
    public void loadAllKeys() {
        List<ActivationKeyDTO> list = keyService.findAll();
        keyList.clear();
        keyList.addAll(list);
    }

    /**
     * Commits key removal.
     */
    public void deleteSelectedKey(ActivationKeyDTO key) {
        if (key != null) {
            keyService.deleteKey(key.getId());
            loadAllKeys();
        }
    }

    /**
     * Executes digital product purchase.
     * @return the full hydrated order details
     */
    public OrderDTO buyGame(UUID userId, UUID gameId) {
        OrderDTO order = orderService.purchaseGame(userId, gameId);
        loadAllGames(); // refresh stock numbers visually
        return order;
    }

    /**
     * Loads order history dynamically depending on context.
     */
    public void loadOrders(UUID userId, boolean isAdmin) {
        List<OrderDTO> list;
        if (isAdmin) {
            list = orderService.findAllOrders();
        } else {
            list = orderService.findOrdersByUser(userId);
        }
        orderList.clear();
        orderList.addAll(list);
    }

    public void deleteCurrentUser(UUID userId, String verificationPwd) {
        userService.deleteAccount(userId, verificationPwd);
    }

    public void loadPublishers() {
        publisherList.clear();
        publisherList.addAll(publisherService.findAll());
    }

    public void loadGenres() {
        genreList.clear();
        genreList.addAll(genreService.findAll());
    }

    public void deleteSelectedPublisher(PublisherDTO publisher) {
        if (publisher != null) {
            publisherService.deletePublisher(publisher.getId());
            loadPublishers();
        }
    }

    public void deleteSelectedGenre(GenreDTO genre) {
        if (genre != null) {
            genreService.deleteGenre(genre.getId());
            loadGenres();
        }
    }

    public void addGenre(String name) {
        genreService.createGenre(name);
        loadGenres();
    }

    public void updateGenre(UUID id, String name) {
        genreService.updateGenre(id, name);
        loadGenres();
    }

    public void addPublisher(String name, String website, String supportEmail) {
        publisherService.createPublisher(name, website, supportEmail);
        loadPublishers();
    }

    public void updatePublisher(UUID id, String name, String website, String supportEmail) {
        publisherService.updatePublisher(id, name, website, supportEmail);
        loadPublishers();
    }

    /**
     * Triggers synchronous byte serialization driving receipt layout extraction.
     */
    public void exportReceipt(OrderDTO order, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            reportGenerator.generateOrderReceipt(order, fos);
        }
    }

    /**
     * Synthesizes consolidated log from existing aggregate list into file sync sink.
     */
    public void exportActiveOrderLog(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            reportGenerator.exportOrderHistory(orderList, fos);
        }
    }
}
