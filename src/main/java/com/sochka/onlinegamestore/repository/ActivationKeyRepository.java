package com.sochka.onlinegamestore.repository;

import com.sochka.onlinegamestore.domain.ActivationKey;
import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivationKeyRepository extends JpaRepository<ActivationKey, UUID> {
    List<ActivationKey> findByGameAndStatus(Game game, KeyStatus status);
    long countByGameAndStatus(Game game, KeyStatus status);
    boolean existsByKeyValue(String keyValue);
}
