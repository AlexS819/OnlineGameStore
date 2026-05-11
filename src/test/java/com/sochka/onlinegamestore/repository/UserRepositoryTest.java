package com.sochka.onlinegamestore.repository;

import com.sochka.onlinegamestore.domain.User;
import com.sochka.onlinegamestore.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Database Integration Test: User Repository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should correctly find persistent users by their unique email addresses")
    void shouldLookUpUserByEmailAddress() {
        // Given
        User user = User.builder()
                .name("Taras Shevchenko")
                .email("taras@kobzar.ua")
                .passwordHash("secureHash123")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("taras@kobzar.ua");
        boolean exists = userRepository.existsByEmail("taras@kobzar.ua");
        boolean notExists = userRepository.existsByEmail("fake@domain.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Taras Shevchenko");
        assertThat(found.get().getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
