package com.ilson.spotwork.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_TOKEN_TTL = 7L;
    private static final String KEY_PREFIX = "refresh:";

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + userId,
                refreshToken,
                REFRESH_TOKEN_TTL,
                TimeUnit.DAYS
        );
    }

    public String find(Long userId) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

    public boolean exists(Long userId) {
        return Boolean.TRUE.equals((redisTemplate.hasKey(KEY_PREFIX + userId)));
    }
}
