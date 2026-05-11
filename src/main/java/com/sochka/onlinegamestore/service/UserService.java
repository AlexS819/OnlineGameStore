package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.UserDTO;
import com.sochka.onlinegamestore.dto.UserRegistrationDTO;

import java.util.List;
import java.util.UUID;

/**
 * Core contract orchestrating secure member authentication and lifecycle.
 */
public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDto);
    UserDTO authenticate(String email, String password);
    UserDTO findById(UUID id);
    List<UserDTO> findAll();
    void deleteAccount(UUID id, String currentPassword);
}
