package org.example.monolithic.order.application;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService {
    private final StringRedisTemplate springRedisTemplate;

    public RedisLockService(StringRedisTemplate springRedisTemplate) {
        this.springRedisTemplate = springRedisTemplate;
    }

    public boolean tryLock(String lockKey, String lockValue) {
        return springRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue);
    }

    public void releaseLock(String lockKey) {
        springRedisTemplate.delete(lockKey);
    }
}
