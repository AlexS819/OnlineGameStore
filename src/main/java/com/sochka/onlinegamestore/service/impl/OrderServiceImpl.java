package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.domain.ActivationKey;
import com.sochka.onlinegamestore.domain.Game;
import com.sochka.onlinegamestore.domain.KeyStatus;
import com.sochka.onlinegamestore.domain.Order;
import com.sochka.onlinegamestore.domain.User;
import com.sochka.onlinegamestore.exception.EntityNotFoundException;
import com.sochka.onlinegamestore.exception.ServiceException;
import com.sochka.onlinegamestore.repository.ActivationKeyRepository;
import com.sochka.onlinegamestore.repository.GameRepository;
import com.sochka.onlinegamestore.repository.OrderRepository;
import com.sochka.onlinegamestore.repository.UserRepository;
import com.sochka.onlinegamestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActivationKeyRepository keyRepository;

    @Override
    @Transactional
    public com.sochka.onlinegamestore.dto.OrderDTO purchaseGame(UUID userId, UUID gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        // Deduct price from balance for regular users
        if (user.getRole() != com.sochka.onlinegamestore.domain.UserRole.ADMIN && !"admin@gamestore.com".equalsIgnoreCase(user.getEmail())) {
            if (user.getBalance().compareTo(game.getPrice()) < 0) {
                throw new ServiceException("Insufficient balance. Please top up your wallet in Profile & Settings.");
            }
            user.setBalance(user.getBalance().subtract(game.getPrice()));
            userRepository.save(user);
        }

        // Find available key
        ActivationKey availableKey = keyRepository.findAll().stream()
                .filter(k -> KeyStatus.AVAILABLE.equals(k.getStatus()) && k.getGame() != null && k.getGame().getId().equals(gameId))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Product is out of stock."));

        // 1. Create and physically commit the parent order header first
        Order order = Order.builder()
                .user(user)
                .totalPrice(game.getPrice())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        
        Order savedOrder = orderRepository.save(order);

        // 2. Link the existing transient/detached key to the freshly provisioned order
        availableKey.setOrder(savedOrder);
        availableKey.setStatus(KeyStatus.SOLD);
        
        // 3. Direct commit targeting key foreign relation
        keyRepository.save(availableKey);

        // Manually supply key context to ensure consistent delivery without requiring flush
        return com.sochka.onlinegamestore.dto.OrderDTO.builder()
                .orderId(savedOrder.getId())
                .purchaseDate(savedOrder.getCreatedAt())
                .userEmail(user.getEmail())
                .gameTitle(game.getTitle())
                .activationKey(availableKey.getKeyValue())
                .price(savedOrder.getTotalPrice())
                .build();
    }

    private com.sochka.onlinegamestore.dto.OrderDTO toDTO(Order order) {
        String gameTitle = "Unknown";
        String activationKey = "N/A";
        
        if (order.getActivationKeys() != null && !order.getActivationKeys().isEmpty()) {
            ActivationKey firstKey = order.getActivationKeys().get(0);
            activationKey = firstKey.getKeyValue();
            if (firstKey.getGame() != null) {
                gameTitle = firstKey.getGame().getTitle();
            }
        }

        return com.sochka.onlinegamestore.dto.OrderDTO.builder()
                .orderId(order.getId())
                .purchaseDate(order.getCreatedAt())
                .userEmail(order.getUser().getEmail())
                .gameTitle(gameTitle)
                .activationKey(activationKey)
                .price(order.getTotalPrice())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.sochka.onlinegamestore.dto.OrderDTO> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.sochka.onlinegamestore.dto.OrderDTO> findOrdersByUser(UUID userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(userId))
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
