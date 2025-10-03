package org.example.monolithic.order.controller;

import org.example.monolithic.order.application.OrderService;
import org.example.monolithic.order.application.RedisLockService;
import org.example.monolithic.order.application.dto.CreateOrderResponse;
import org.example.monolithic.order.application.dto.CreateOrderResult;
import org.example.monolithic.order.controller.dto.CreateOrderRequest;
import org.example.monolithic.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final RedisLockService redisLockService;

    public OrderController(OrderService orderService, RedisLockService redisLockService) {
        this.orderService = orderService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCreateOrderCommand());

        return new CreateOrderResponse(result.orderId());
    }

    @PostMapping("/order/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) throws InterruptedException {
        String lockKey = "order:monolithic:" + request.orderId();
        boolean acquiredLock = redisLockService.tryLock(lockKey, request.orderId().toString());

        if(!acquiredLock) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            orderService.placeOrder(request.toPlaceOrderCommand());
        }
        finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
