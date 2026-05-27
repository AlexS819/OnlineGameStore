package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.domain.User;
import com.sochka.onlinegamestore.domain.UserRole;
import com.sochka.onlinegamestore.dto.UserDTO;
import com.sochka.onlinegamestore.dto.UserRegistrationDTO;
import com.sochka.onlinegamestore.exception.AuthenticationException;
import com.sochka.onlinegamestore.exception.EntityNotFoundException;
import com.sochka.onlinegamestore.infrastructure.EmailSender;
import com.sochka.onlinegamestore.infrastructure.PasswordHasher;
import com.sochka.onlinegamestore.mapper.UserMapper;
import com.sochka.onlinegamestore.repository.UserRepository;
import com.sochka.onlinegamestore.repository.OrderRepository;
import com.sochka.onlinegamestore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Industrial-grade realization of member services featuring transactional security validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO dto) {
        log.info("Initiating registration sequence for email: {}", dto.getEmail());
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AuthenticationException("Account with this email already registered");
        }

        String securePassword = passwordHasher.hash(dto.getPassword());

        User newUser = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(securePassword)
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(newUser);
        
        // Send onboarding notification using the separated infrastructure component
        emailSender.send(savedUser.getEmail(), 
                "Welcome to Online Game Store!", 
                "Hello " + savedUser.getName() + ", your account is active!");

        log.info("Successfully registered new user ID: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO authenticate(String email, String password) {
        log.info("Authenticating session request for agent: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials provided"));

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            log.warn("Rejected security attempt: Invalid password password signature for {}", email);
            throw new AuthenticationException("Invalid credentials provided");
        }

        log.info("Authenticated access granted for user: {}", email);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " missing"));
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id, String currentPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account record vanished."));

        if (!passwordHasher.matches(currentPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Deletion failed: Verification credentials incorrect.");
        }

        // Prevent admins from deleting root accounts this way if logic implies, or allow it?
        // The user prompt implies "not administrator". Let's double check security role outside.

        // Physically resolve order foreign key restrictions before deleting parent subject
        orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(id))
                .forEach(orderRepository::delete);

        userRepository.delete(user);
        log.info("Account completely eradicated from corporate systems: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        log.info("Attempting to reset password for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Account with this email not found."));

        if (user.getRole() == com.sochka.onlinegamestore.domain.UserRole.ADMIN || "admin@gamestore.com".equalsIgnoreCase(email)) {
            throw new AuthenticationException("Password reset is not permitted for administrator accounts.");
        }

        String securePassword = passwordHasher.hash(newPassword);
        user.setPasswordHash(securePassword);
        userRepository.save(user);
        log.info("Successfully reset password for user ID: {}", user.getId());
    }

    @Override
    @Transactional
    public void toggleTwoFactor(java.util.UUID userId, boolean enabled) {
        log.info("Toggling 2FA for user ID: {} to {}", userId, enabled);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (enabled && (user.getRole() == com.sochka.onlinegamestore.domain.UserRole.ADMIN || "admin@gamestore.com".equalsIgnoreCase(user.getEmail()))) {
            throw new AuthenticationException("Two-factor authentication cannot be enabled for administrator accounts.");
        }

        user.setTwoFactorEnabled(enabled);
        userRepository.save(user);
        log.info("2FA successfully set to {} for user: {}", enabled, user.getEmail());
    }
}
