package com.sochka.onlinegamestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationKeyDTO {
    private UUID id;
    private String keyValue;
    private String status; // e.g., AVAILABLE, SOLD
    private UUID gameId;
    private String gameTitle;
    private java.time.LocalDateTime soldAt;
    private String buyerEmail;
}
