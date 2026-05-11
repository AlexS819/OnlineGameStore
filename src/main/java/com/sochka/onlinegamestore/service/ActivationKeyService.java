package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.ActivationKeyDTO;

import java.util.List;
import java.util.UUID;

/**
 * Business Service handling management lifecycle of activation key licenses.
 */
public interface ActivationKeyService {
    
    /**
     * Retrieves complete aggregate list of all existing keys regardless of status.
     */
    List<ActivationKeyDTO> findAll();

    /**
     * Fetches unused available licenses tied to specific digital product.
     */
    List<ActivationKeyDTO> findAvailableByGame(UUID gameId);

    /**
     * Provisions fresh activation license string to specified product.
     * @throws IllegalArgumentException if duplicate key detected
     */
    ActivationKeyDTO addKeyToGame(UUID gameId, String keyValue);

    /**
     * Updates data bounds or string content of existing license node.
     */
    ActivationKeyDTO updateKey(UUID keyId, UUID gameId, String keyValue);

    /**
     * Permanently removes license node from storage tier.
     */
    void deleteKey(UUID keyId);
}
