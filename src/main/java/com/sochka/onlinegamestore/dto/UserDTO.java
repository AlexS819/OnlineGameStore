package com.sochka.onlinegamestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * User profile state projected safely excluding secure parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private boolean twoFactorEnabled;
    private java.math.BigDecimal balance;
}
