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
public class GenreDTO {
    private UUID id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
