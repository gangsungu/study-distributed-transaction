package org.example.point.controller;

import org.example.point.application.PointFacadeService;
import org.example.point.application.RedisLockService;
import org.example.point.controller.dto.PointReserveRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {

    private final PointFacadeService pointFacadeService;
    private final RedisLockService redisLockService;

    public PointController(PointFacadeService pointFacadeService, RedisLockService redisLockService) {
        this.pointFacadeService = pointFacadeService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/point/reserve")
    public void reservePoint(@RequestBody PointReserveRequest request) {
        String key = "point:" + request.requestId();
        boolean acquiredLock = redisLockService.tryLock(key, request.requestId());

        if(!acquiredLock) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            pointFacadeService.tryReserve(request.toCommand());
        }
        finally {
            redisLockService.releaseLock(key);
        }
    }
}
