package com.sochka.onlinegamestore.repository;

import com.sochka.onlinegamestore.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByPriceLessThanEqual(BigDecimal maxPrice);
}
