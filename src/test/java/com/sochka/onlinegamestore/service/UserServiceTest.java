package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.UserDTO;
import com.sochka.onlinegamestore.dto.UserRegistrationDTO;
import com.sochka.onlinegamestore.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Business Logic Test: User Service")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("Should securely register user and subsequently authenticate session")
    void shouldRegisterAndLoginSuccessfully() {
        // 1. Setup registration request
        UserRegistrationDTO regDto = UserRegistrationDTO.builder()
                .name("Ivan Franko")
                .email("ivan@kamenyar.ua")
                .password("topSecret123")
                .build();

        // 2. Execute registration logic
        UserDTO registeredUser = userService.registerUser(regDto);

        // 3. Verify persistence & transform
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getName()).isEqualTo("Ivan Franko");
        assertThat(registeredUser.getEmail()).isEqualTo("ivan@kamenyar.ua");

        // 4. Attempt secure authentication
        UserDTO loggedIn = userService.authenticate("ivan@kamenyar.ua", "topSecret123");
        
        assertThat(loggedIn.getId()).isEqualTo(registeredUser.getId());
    }

    @Test
    @DisplayName("Should reject registration attempts using duplicate emails")
    void shouldBlockDuplicateEmailRegistration() {
        UserRegistrationDTO regDto = UserRegistrationDTO.builder()
                .name("Clone User")
                .email("duplicate@system.com")
                .password("pass")
                .build();

        userService.registerUser(regDto);

        // Attempting second time with same email
        assertThrows(AuthenticationException.class, () -> {
            userService.registerUser(regDto);
        });
    }

    @Test
    @DisplayName("Should block access given invalid password signature")
    void shouldRefuseInvalidCredentials() {
        UserRegistrationDTO regDto = UserRegistrationDTO.builder()
                .name("Security Target")
                .email("target@secure.net")
                .password("validPassword")
                .build();

        userService.registerUser(regDto);

        // Assert authentication throws exception on bad password
        assertThrows(AuthenticationException.class, () -> {
            userService.authenticate("target@secure.net", "WRONG_PASSWORD");
        });
    }
}
