package com.bird.lock.redis;

import com.bird.lock.AbstractDistributedLock;
import com.bird.lock.redis.configuration.RedisLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxx
 * @since 2020/10/26
 */
@Slf4j
public class RedisDistributedLock  extends AbstractDistributedLock {

    private final StringRedisTemplate redisTemplate;
    private final RedisLockProperties redisLockProperties;

    public RedisDistributedLock(StringRedisTemplate redisTemplate, RedisLockProperties redisLockProperties) {
        this.redisTemplate = redisTemplate;
        this.redisLockProperties = redisLockProperties;
    }

    @Override
    public void lock(String lockKey) {
        this.lock(lockKey, this.redisLockProperties.getKeyExpire());
    }

    @Override
    public void lock(String lockKey, long keyExpire) {
        super.lock(lockKey, keyExpire, this.redisLockProperties.getRetryInterval());
    }

    @Override
    public boolean tryLock(String lockKey) {
        return this.tryLock(lockKey, this.redisLockProperties.getKeyExpire());
    }

    @Override
    public boolean tryLock(String lockKey, long keyExpire) {
        return this.tryLock(lockKey, keyExpire, this.redisLockProperties.getRetryExpire());
    }

    @Override
    public boolean tryLock(String lockKey, long keyExpire, long retryExpire) {
        return super.tryLock(lockKey, keyExpire, this.redisLockProperties.getRetryInterval(), retryExpire);
    }

    @Override
    public boolean tryLock(String lockKey, String lockValue, long expire) {
        // 尝试加锁
        return redisTemplate.opsForValue().setIfAbsent(this.redisLockProperties.getKeyPrefix() + lockKey, lockValue, expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean releaseLock(String lockKey, String lockValue) {
        String key = this.redisLockProperties.getKeyPrefix() + lockKey;

        String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(lua, Integer.class);
        Integer result = redisTemplate.execute(redisScript, Collections.singletonList(key), lockValue);
        return Objects.equals(result, 1);
    }
}
