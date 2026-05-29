package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.exception.EntityNotFoundException;
import com.sochka.onlinegamestore.mapper.GameMapper;
import com.sochka.onlinegamestore.repository.GameRepository;
import com.sochka.onlinegamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.Publisher;
import com.sochka.onlinegamestore.domain.KeyStatus;
import com.sochka.onlinegamestore.repository.ActivationKeyRepository;
import com.sochka.onlinegamestore.repository.GenreRepository;
import com.sochka.onlinegamestore.repository.PublisherRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Optimized data pipeline realizing efficient catalog delivery.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final ActivationKeyRepository keyRepository;
    private final GameMapper gameMapper;

    private GameDTO toDTOWithStock(Game game) {
        GameDTO dto = gameMapper.toDTO(game);
        long availableKeys = keyRepository.findAll().stream()
                .filter(k -> KeyStatus.AVAILABLE.equals(k.getStatus()) && k.getGame() != null && k.getGame().getId().equals(game.getId()))
                .count();
        dto.setAvailableKeysCount((int) availableKeys);
        return dto;
    }

    @Override
    public List<GameDTO> findAll() {
        return gameRepository.findAll().stream()
                .map(this::toDTOWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> searchByTitle(String keyword) {
        return gameRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::toDTOWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> filterByMaxPrice(BigDecimal maxPrice) {
        return gameRepository.findByPriceLessThanEqual(maxPrice).stream()
                .map(this::toDTOWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public GameDTO findById(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requested catalog asset non-existent."));
        return toDTOWithStock(game);
    }

    @Override
    @Transactional
    public GameDTO createGame(String title, BigDecimal price, UUID publisherId, java.util.Set<UUID> genreIds, String description, String imageUrl) {
        Publisher pub = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new EntityNotFoundException("Referenced publisher missing from archives."));

        java.util.Set<com.sochka.onlinegamestore.domain.Genre> mappedGenres = new HashSet<>();
        if (genreIds != null && !genreIds.isEmpty()) {
            mappedGenres.addAll(genreRepository.findAllById(genreIds));
        }

        Game newGame = Game.builder()
                .title(title)
                .price(price)
                .publisher(pub)
                .genres(mappedGenres)
                .description(description)
                .imageUrl(imageUrl)
                .build();

        Game saved = gameRepository.save(newGame);
        return toDTOWithStock(saved);
    }

    @Override
    @Transactional
    public GameDTO updateGame(UUID id, String title, BigDecimal price, UUID publisherId, java.util.Set<UUID> genreIds, String description, String imageUrl) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Update failed: Target record missing."));
        
        Publisher pub = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new EntityNotFoundException("Update failed: Referenced publisher missing."));

        game.setTitle(title);
        game.setPrice(price);
        game.setPublisher(pub);
        game.setDescription(description);
        game.setImageUrl(imageUrl);
        
        java.util.Set<com.sochka.onlinegamestore.domain.Genre> mappedGenres = new HashSet<>();
        if (genreIds != null && !genreIds.isEmpty()) {
            mappedGenres.addAll(genreRepository.findAllById(genreIds));
        }
        game.setGenres(mappedGenres);

        Game saved = gameRepository.save(game);
        return toDTOWithStock(saved);
    }

    @Override
    @Transactional
    public void deleteGame(UUID id) {
        if (!gameRepository.existsById(id)) {
            throw new EntityNotFoundException("Delete failed: Product registry entry non-existent.");
        }
        gameRepository.deleteById(id);
    }
}
