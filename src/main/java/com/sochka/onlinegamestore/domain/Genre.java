package com.sochka.onlinegamestore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Entity representing a product genre classification.
 * Forms part of a Many-to-Many relationship with Game catalog.
 */
@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Genre extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;
}
