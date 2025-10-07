package org.example.point.controller.dto;

public record PointReserveCancelRequest(String requestId) {

    public PointReserveCancelCommand toCommand() {
        return new PointReserveCancelCommand(requestId);
    }
}
