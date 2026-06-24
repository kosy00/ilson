package com.ilson.spotwork.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String KEY_PREFIX = "refresh:";

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + userId,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
    }

    public Optional<String> find(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + userId));
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

    public boolean exists(Long userId) {
        return Boolean.TRUE.equals((redisTemplate.hasKey(KEY_PREFIX + userId)));
    }

    // 토큰 일치 확인 후 삭제를 원자적으로 수행 (RTR 경쟁 조건 방지)
    public boolean compareAndDelete(Long userId, String refreshToken) {
        String script =
                "local stored = redis.call('GET', KEYS[1]) " +
                "if stored == ARGV[1] then " +
                "  redis.call('DEL', KEYS[1]) " +
                "  return 1 " +
                "else " +
                "  return 0 " +
                "end";
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                List.of(KEY_PREFIX + userId),
                refreshToken
        );
        return Long.valueOf(1).equals(result);
    }
}
