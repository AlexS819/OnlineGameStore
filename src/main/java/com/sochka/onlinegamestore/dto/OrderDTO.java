package com.sochka.onlinegamestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private LocalDateTime purchaseDate;
    private String userEmail;
    private String gameTitle;
    private String activationKey;
    private BigDecimal price;
}
