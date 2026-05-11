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
public class PublisherDTO {
    private UUID id;
    private String name;
    private String website;
    private String supportEmail;
    
    @Override
    public String toString() {
        return name; // Essential for auto-display inside JavaFX ComboBox
    }
}
