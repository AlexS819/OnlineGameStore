package com.sochka.onlinegamestore.service;

import java.util.UUID;

public interface OrderService {
    /**
     * Executes purchase transaction binding an available key to the customer.
     * @param userId buyer identifier
     * @param gameId target game identifier
     * @return the resulting transaction details for receipts
     */
    com.sochka.onlinegamestore.dto.OrderDTO purchaseGame(UUID userId, UUID gameId);

    /**
     * Extracts universal list encompassing global transactional operational telemetry.
     */
    java.util.List<com.sochka.onlinegamestore.dto.OrderDTO> findAllOrders();

    /**
     * Retrieves transactional footprints specifically executed by individual actor.
     */
    java.util.List<com.sochka.onlinegamestore.dto.OrderDTO> findOrdersByUser(UUID userId);
}
