package org.example.product.controller;

import org.example.product.application.ProductFacadeService;
import org.example.product.application.RedisLockService;
import org.example.product.application.dto.ProductReserveResult;
import org.example.product.controller.dto.ProductReserveRequest;
import org.example.product.controller.dto.ProductReserveResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductFacadeService productFacadeService;
    private final RedisLockService redisLockService;

    public ProductController(ProductFacadeService productFacadeService, RedisLockService redisLockService) {
        this.productFacadeService = productFacadeService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/product/reserve")
    public ProductReserveResponse reserveProduct(@RequestBody ProductReserveRequest request) {
        System.out.println("Reserving product");
        String key = "product:" + request.requestId();
        boolean acquiredLock = redisLockService.tryLock(key, request.requestId());

        if(!acquiredLock) {
            throw new RuntimeException("락 획득에 실패했습니다.");
        }

        try {
            ProductReserveResult result = productFacadeService.tryReserve(request.toCommand());
            return new ProductReserveResponse(result.totalPrice());
        }
        finally {
            redisLockService.releaseLock(key);
        }
    }
}
