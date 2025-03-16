package com.side.anything.back.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value);
    }

    public void setValue(String key, String value, Duration duration) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value, duration);
    }

    public String getValue(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

}
