package org.example.point.controller.dto;

import org.example.point.application.dto.PointReserveConfirmCommand;

public record PointReserveConfirmRequest(String requestId) {

    public PointReserveConfirmCommand toCommand() {
        return new PointReserveConfirmCommand(requestId);
    }
}
