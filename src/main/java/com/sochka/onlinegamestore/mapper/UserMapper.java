package com.sochka.onlinegamestore.mapper;

import com.sochka.onlinegamestore.domain.User;
import com.sochka.onlinegamestore.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Transformation bridge isolating confidential model structures from transport contracts.
 */
@Component
public class UserMapper {

    public UserDTO toDTO(User entity) {
        if (entity == null) return null;
        
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole() != null ? entity.getRole().name() : "USER")
                .build();
    }
}
