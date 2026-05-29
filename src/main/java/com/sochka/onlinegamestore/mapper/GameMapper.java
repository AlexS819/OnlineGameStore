package com.sochka.onlinegamestore.mapper;

import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.Genre;
import com.sochka.onlinegamestore.dto.GameDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Dynamic object transform pipeline projection for Product Catalog entities.
 */
@Component
public class GameMapper {

    public GameDTO toDTO(Game entity) {
        if (entity == null) return null;
        
        return GameDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .price(entity.getPrice())
                .publisherName(entity.getPublisher() != null ? entity.getPublisher().getName() : "Unknown")
                .publisherId(entity.getPublisher() != null ? entity.getPublisher().getId() : null)
                .genreIds(entity.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .genreNames(entity.getGenres().stream().map(Genre::getName).collect(Collectors.toSet()))
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
