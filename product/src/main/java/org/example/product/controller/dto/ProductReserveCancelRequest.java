package org.example.product.controller.dto;

import org.example.product.application.dto.ProductReserveCancelCommand;

public record ProductReserveCancelRequest(String requestId) {

    public ProductReserveCancelCommand toCommand() {
        return new ProductReserveCancelCommand(requestId);
    }
}
