package com.sochka.onlinegamestore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Standard user access account record.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = "passwordHash")
public class User extends BaseEntity implements Comparable<User> {

    @NotBlank(message = "User name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 chars")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Format must be valid email")
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password storage is mandatory")
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private UserRole role;

    @Override
    public int compareTo(User o) {
        return this.name.compareTo(o.name);
    }
}