package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.domain.ActivationKey;
import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.KeyStatus;
import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.exception.EntityNotFoundException;
import com.sochka.onlinegamestore.repository.ActivationKeyRepository;
import com.sochka.onlinegamestore.repository.GameRepository;
import com.sochka.onlinegamestore.service.ActivationKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivationKeyServiceImpl implements ActivationKeyService {

    private final ActivationKeyRepository keyRepository;
    private final GameRepository gameRepository;

    private ActivationKeyDTO toDTO(ActivationKey key) {
        return ActivationKeyDTO.builder()
                .id(key.getId())
                .keyValue(key.getKeyValue())
                .status(key.getStatus() != null ? key.getStatus().getDisplayName() : "N/A")
                .gameId(key.getGame() != null ? key.getGame().getId() : null)
                .gameTitle(key.getGame() != null ? key.getGame().getTitle() : "Unknown")
                .soldAt(key.getOrder() != null ? key.getOrder().getCreatedAt() : null)
                .buyerEmail(key.getOrder() != null && key.getOrder().getUser() != null ? key.getOrder().getUser().getEmail() : "-")
                .build();
    }

    @Override
    public List<ActivationKeyDTO> findAll() {
        return keyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivationKeyDTO> findAvailableByGame(UUID gameId) {
        return keyRepository.findAll().stream()
                .filter(k -> KeyStatus.AVAILABLE.equals(k.getStatus()) && k.getGame() != null && k.getGame().getId().equals(gameId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivationKeyDTO addKeyToGame(UUID gameId, String keyValue) {
        if (keyRepository.existsByKeyValue(keyValue)) {
            throw new IllegalArgumentException("Operational Failure: An identical activation key already exists within persistent architecture.");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found for ID: " + gameId));

        ActivationKey newKey = ActivationKey.builder()
                .keyValue(keyValue)
                .status(KeyStatus.AVAILABLE)
                .game(game)
                .build();

        ActivationKey saved = keyRepository.save(newKey);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public ActivationKeyDTO updateKey(UUID keyId, UUID gameId, String keyValue) {
        ActivationKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new EntityNotFoundException("Activation key not found."));
                
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found."));

        // Defensive redundancy guarding during mutation state transit
        if (keyValue != null && !keyValue.equals(key.getKeyValue())) {
            if (keyRepository.existsByKeyValue(keyValue)) {
                throw new IllegalArgumentException("Operational Failure: Identity conflict detected. Target activation key exists elsewhere.");
            }
            key.setKeyValue(keyValue);
        }
        key.setGame(game);

        ActivationKey saved = keyRepository.save(key);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteKey(UUID keyId) {
        if (!keyRepository.existsById(keyId)) {
            throw new EntityNotFoundException("Activation key not found.");
        }
        keyRepository.deleteById(keyId);
    }
}
