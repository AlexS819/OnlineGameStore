package com.sochka.onlinegamestore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Product manufacturer/distribution agent persistence unit.
 */
@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Publisher extends BaseEntity {

    @NotBlank(message = "Publisher name is required")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", message = "Invalid website URL")
    @Column(name = "website", length = 255)
    private String website;

    @Email(message = "Valid support email required")
    @Column(name = "support_email", length = 255)
    private String supportEmail;
}
