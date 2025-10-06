package org.example.product.controller.dto;

import org.example.product.application.dto.ProductReserveConfirmCommand;

public record ProductReserveConfirmRequest(String requestId) {
    public ProductReserveConfirmCommand toCommand() {
        return new ProductReserveConfirmCommand(requestId);
    }
}
