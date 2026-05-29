package com.sochka.onlinegamestore;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;

/**
 * Native graphical foundation adapter loading the active application ecosystem.
 */
public class JavaFxApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Bootstrap the Spring subsystem quietly in the background before UI renders
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.springContext = new SpringApplicationBuilder()
                .sources(OnlineGameStoreApplication.class)
                .run(args);

        // Asynchronously pre-cache game cover images
        java.util.concurrent.ForkJoinPool.commonPool().execute(() -> {
            try {
                com.sochka.onlinegamestore.repository.GameRepository gameRepo = 
                    springContext.getBean(com.sochka.onlinegamestore.repository.GameRepository.class);
                java.util.List<com.sochka.onlinegamestore.domain.Game> games = gameRepo.findAll();
                java.util.List<String> urls = new java.util.ArrayList<>();
                for (com.sochka.onlinegamestore.domain.Game game : games) {
                    if (game.getImageUrl() != null && !game.getImageUrl().trim().isEmpty()) {
                        urls.add(game.getImageUrl());
                    }
                }
                com.sochka.onlinegamestore.utils.ImageCache.preCacheImages(urls);
            } catch (Exception e) {
                System.err.println("Error pre-caching images: " + e.getMessage());
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Define root layout template from pre-built resources
        URL layoutResource = getClass().getResource("/views/login.fxml");
        if (layoutResource == null) {
             throw new RuntimeException("Crucial resource missing: /views/login.fxml not found");
        }

        FXMLLoader loader = new FXMLLoader(layoutResource);
        
        // MAGIC LINK: Allow Spring to provide instances for controllers!
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        primaryStage.setTitle("Online Game Store - Authorization");
        try {
            primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/icon.png")));
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Gracefully discharge all database and thread pools
        springContext.close();
        Platform.exit();
    }
}
