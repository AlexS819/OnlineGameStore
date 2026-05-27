package com.sochka.onlinegamestore;

import com.sochka.onlinegamestore.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.sochka.onlinegamestore.domain.User;
import com.sochka.onlinegamestore.infrastructure.PasswordHasher;
import com.sochka.onlinegamestore.repository.UserRepository;

/**
 * Main entry point for the Online Game Store application subsystem. Initializes Spring framework
 * context, component scanning, and database configurations.
 */
@SpringBootApplication
@Slf4j
public class OnlineGameStoreApplication {

    public static void main(String[] args) {
        com.sochka.onlinegamestore.infrastructure.EnvLoader.load();
        Application.launch(JavaFxApp.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner run(GameRepository gameRepository, UserRepository userRepository, PasswordHasher hasher) {
        return args -> {
            log.info("=============================================================");
            log.info(" SYSTEM PRE-FLIGHT CHECKS INITIATED");
            
            // Ensure admin has a VALID BCrypt hash for demo purposes
            userRepository.findByEmail("admin@gamestore.com").ifPresent(user -> {
                user.setPasswordHash(hasher.hash("admin12345"));
                userRepository.save(user);
                log.info(" -> Validated and fixed administrative hash for 'admin@gamestore.com'");
            });

            log.info(" Found {} games pre-loaded in the database catalog.", gameRepository.count());
            log.info("=============================================================");
        };
    }
}
