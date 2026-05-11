package com.sochka.onlinegamestore.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Central transition administrator coordinating live scene injections through application context factory.
 */
@Component
@RequiredArgsConstructor
public class SceneSwitcher {

    private final ApplicationContext context;

    public void switchScene(Stage currentStage, String fxmlPath, String title, int width, int height) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file location unresolved: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            
            // Direct injection linkage 
            loader.setControllerFactory(context::getBean);
            
            Parent root = loader.load();
            Scene newScene = new Scene(root, width, height);
            
            currentStage.setTitle(title);
            currentStage.setScene(newScene);
            currentStage.centerOnScreen();
            currentStage.setResizable(true);
            currentStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("CRITICAL UI FAILURE: Terminal transition failure to " + fxmlPath, e);
        }
    }
}
