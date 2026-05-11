package com.sochka.onlinegamestore.repository;

import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.Publisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Database Integration Test: Game Repository")
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    @DisplayName("Should successfully save a game and relate it to a publisher")
    void shouldPersistAndLinkGameToPublisher() {
        // Given
        Publisher publisher = Publisher.builder()
                .name("CD Projekt RED")
                .supportEmail("support@cdprojekt.com")
                .build();
        Publisher savedPublisher = publisherRepository.save(publisher);

        Game game = Game.builder()
                .title("The Witcher 3: Wild Hunt")
                .price(new BigDecimal("39.99"))
                .publisher(savedPublisher)
                .build();

        // When
        Game savedGame = gameRepository.save(game);

        // Then
        assertThat(savedGame.getId()).isNotNull();
        assertThat(savedGame.getTitle()).isEqualTo("The Witcher 3: Wild Hunt");
        
        Optional<Game> retrieved = gameRepository.findById(savedGame.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPublisher().getName()).isEqualTo("CD Projekt RED");
    }

    @Test
    @DisplayName("Should filter games by price range")
    void shouldFindGamesByPriceLimits() {
        // Setup
        Publisher publisher = publisherRepository.save(Publisher.builder().name("TestPub").build());
        gameRepository.save(Game.builder().title("Cheap Game").price(new BigDecimal("5.00")).publisher(publisher).build());
        gameRepository.save(Game.builder().title("Expensive Game").price(new BigDecimal("100.00")).publisher(publisher).build());

        // When
        List<Game> result = gameRepository.findByPriceLessThanEqual(new BigDecimal("10.00"));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Cheap Game");
    }
}
