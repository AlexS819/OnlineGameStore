package com.sochka.onlinegamestore.infrastructure;

import com.sochka.onlinegamestore.dto.OrderDTO;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Standard enterprise protocol for synthesizing digital documents from application dataset payloads.
 */
public interface ReportGenerator {
    
    /**
     * Compiles individual purchase record into branded structured ledger sheet.
     */
    void generateOrderReceipt(OrderDTO order, OutputStream output) throws IOException;

    /**
     * Renders unified operational grid from global transaction aggregate.
     */
    void exportOrderHistory(List<OrderDTO> orders, OutputStream output) throws IOException;
}
