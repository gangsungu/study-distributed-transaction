package org.example.monolithic.order.controller.dto;

import org.example.monolithic.order.application.dto.PlaceOrderCommand;

public record PlaceOrderRequest(
    Long orderId
) {

    public PlaceOrderCommand toPlaceOrderCommand() {
        return new PlaceOrderCommand(orderId);
    }
}
