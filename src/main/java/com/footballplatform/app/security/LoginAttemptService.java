package com.footballplatform.app.security;

import java.time.Duration;
import java.util.Locale;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAX_LOGIN_REQUESTS_PER_MINUTE = 20;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);
    private static final Duration FAILED_ATTEMPT_WINDOW = Duration.ofMinutes(15);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;

    public LoginAttemptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isRateLimited(String ipAddress) {
        String key = buildRateLimitKey(ipAddress);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, RATE_LIMIT_WINDOW);
        }
        return count != null && count > MAX_LOGIN_REQUESTS_PER_MINUTE;
    }

    public boolean isLocked(String ipAddress, String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildLockKey(ipAddress, username)));
    }

    public boolean recordFailure(String ipAddress, String username) {
        String failKey = buildFailureKey(ipAddress, username);
        Long count = redisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            redisTemplate.expire(failKey, FAILED_ATTEMPT_WINDOW);
        }

        if (count != null && count >= MAX_FAILED_ATTEMPTS) {
            redisTemplate.opsForValue().set(buildLockKey(ipAddress, username), "1", LOCK_DURATION);
            return true;
        }

        return false;
    }

    public void clearFailures(String ipAddress, String username) {
        redisTemplate.delete(buildFailureKey(ipAddress, username));
        redisTemplate.delete(buildLockKey(ipAddress, username));
    }

    private String buildRateLimitKey(String ipAddress) {
        return "login:rl:" + normalizeIp(ipAddress);
    }

    private String buildFailureKey(String ipAddress, String username) {
        return "login:fail:" + normalizeIp(ipAddress) + ":" + normalizeUsername(username);
    }

    private String buildLockKey(String ipAddress, String username) {
        return "login:lock:" + normalizeIp(ipAddress) + ":" + normalizeUsername(username);
    }

    private String normalizeIp(String ipAddress) {
        return ipAddress == null || ipAddress.isBlank() ? "unknown" : ipAddress.trim();
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return "anonymous";
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
