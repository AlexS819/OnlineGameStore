package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.GameDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Domain contract for interacting with available catalog inventories.
 */
public interface GameService {
    
    /**
     * Retrieves total visual catalog encompassing available title counts.
     */
    List<GameDTO> findAll();

    /**
     * Filters inventory through case-insensitive partial string match analysis.
     */
    List<GameDTO> searchByTitle(String keyword);

    /**
     * Restricts returned collection to inclusive maximal price boundaries.
     */
    List<GameDTO> filterByMaxPrice(BigDecimal maxPrice);

    /**
     * Accesses specialized domain metrics by explicit identity key.
     */
    GameDTO findById(UUID id);
    
    /**
     * Synthesizes and persists new digital product with multi-dimensional relation linking.
     */
    GameDTO createGame(String title, BigDecimal price, UUID publisherId, java.util.Set<UUID> genreIds);

    /**
     * Edits attributes and re-associates existing product to updated domain subsets.
     */
    GameDTO updateGame(UUID id, String title, BigDecimal price, UUID publisherId, java.util.Set<UUID> genreIds);

    /**
     * Safely detaches and removes title node provided constraints permit destruction.
     */
    void deleteGame(UUID id);
}
