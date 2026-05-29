package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.dto.GameDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class GameDetailsController {

    @FXML private Label gameTitleLabel;
    @FXML private Label publisherLabel;
    @FXML private ImageView artworkImageView;
    @FXML private FlowPane genresFlowPane;
    @FXML private Label descriptionLabel;
    @FXML private Label stockStatusLabel;
    @FXML private Label priceLabel;

    @FXML private Button closeTopBtn;
    @FXML private Button closeBtn;
    @FXML private Button buyBtn;

    private GameDTO game;
    private Runnable onBuyHandler;

    @FXML
    public void initialize() {
        closeTopBtn.setOnAction(e -> closeDialog());
        closeBtn.setOnAction(e -> closeDialog());
        buyBtn.setOnAction(e -> {
            closeDialog();
            if (onBuyHandler != null) {
                onBuyHandler.run();
            }
        });
    }

    public void setGame(GameDTO game, Runnable onBuyHandler) {
        this.game = game;
        this.onBuyHandler = onBuyHandler;

        gameTitleLabel.setText(game.getTitle());
        publisherLabel.setText("by " + game.getPublisherName());
        priceLabel.setText("$" + game.getPrice());

        // Update description
        if (game.getDescription() != null && !game.getDescription().trim().isEmpty()) {
            descriptionLabel.setText(game.getDescription());
        } else {
            descriptionLabel.setText("No description provided for this game.");
        }

        // Update Stock count
        if (game.getAvailableKeysCount() > 0) {
            stockStatusLabel.setText("In Stock (" + game.getAvailableKeysCount() + ")");
            stockStatusLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold;");
            buyBtn.setDisable(false);
            buyBtn.setText("Buy Now");
        } else {
            stockStatusLabel.setText("OUT OF STOCK");
            stockStatusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            buyBtn.setDisable(true);
            buyBtn.setText("Out of Stock");
        }

        // Load image artwork
        String imgUrl = game.getImageUrl();
        if (imgUrl != null && !imgUrl.trim().isEmpty()) {
            try {
                Image image = com.sochka.onlinegamestore.utils.ImageCache.getCachedImage(imgUrl);
                artworkImageView.setImage(image);
            } catch (Exception ex) {
                loadPlaceholderImage();
            }
        } else {
            loadPlaceholderImage();
        }

        // Populating genre badges
        genresFlowPane.getChildren().clear();
        if (game.getGenreNames() != null && !game.getGenreNames().isEmpty()) {
            for (String genreName : game.getGenreNames()) {
                Label badge = new Label(genreName);
                badge.setStyle("-fx-background-color: #E8F0FE; -fx-text-fill: #1877F2; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 4px 12px;");
                genresFlowPane.getChildren().add(badge);
            }
        } else {
            Label badge = new Label("General");
            badge.setStyle("-fx-background-color: #E4E6EB; -fx-text-fill: #4B4F56; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 4px 12px;");
            genresFlowPane.getChildren().add(badge);
        }
    }

    private void loadPlaceholderImage() {
        // Fallback to online generic cover placeholder
        try {
            Image image = com.sochka.onlinegamestore.utils.ImageCache.getCachedImage("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&q=80");
            artworkImageView.setImage(image);
        } catch (Exception e) {
            artworkImageView.setImage(null);
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
