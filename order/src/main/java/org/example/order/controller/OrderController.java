package org.example.order.controller;

import org.example.order.application.OrderCoordinator;
import org.example.order.application.OrderService;
import org.example.order.application.RedisLockService;
import org.example.order.application.dto.CreateOrderResult;
import org.example.order.controller.dto.CreateOrderRequest;
import org.example.order.controller.dto.CreateOrderResponse;
import org.example.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final OrderCoordinator orderCoordinator;
    private final RedisLockService redisLockService;

    public OrderController(OrderService orderService, OrderCoordinator orderCoordinator, RedisLockService redisLockService) {
        this.orderService = orderService;
        this.orderCoordinator = orderCoordinator;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCreateOrderCommand());

        return new CreateOrderResponse(result.orderId());
    }

    @PostMapping("/order/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) {
        String lockKey = "order:monolithic:" + request.orderId();
        boolean acquiredLock = redisLockService.tryLock(lockKey, request.orderId().toString());

        if(!acquiredLock) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            orderCoordinator.placeOrder(request.toCommand());
        }
        finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
