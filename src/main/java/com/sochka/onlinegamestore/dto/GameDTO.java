package com.sochka.onlinegamestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Flat data view of a gaming product suitable for UI rendering.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private UUID id;
    private String title;
    private BigDecimal price;
    private UUID publisherId;
    private String publisherName;
    private Set<UUID> genreIds;
    private Set<String> genreNames;
    private int availableKeysCount;
    private String description;
    private String imageUrl;

}
